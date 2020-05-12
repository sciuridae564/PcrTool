package cn.sciuridae.CoolQ.Listener;

import cn.sciuridae.DB.bean.*;
import cn.sciuridae.DB.sqLite.DB;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;

import static cn.sciuridae.Tools.stringTool.*;
import static cn.sciuridae.constant.*;

@Beans
public class prcnessListener {

    private static HashMap<String, String> coolDown;//抽卡冷却时间

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#帮助.*" ,at = true)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), helpMsg);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = "#帮助")
    public void testListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, helpMsg);
    }

    /**
     * 格式 #建会@机器人 工会名 自己的游戏名字（没有@工会长默认创建者为工会长）
     * @param msg
     * @param sender
     */
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#建会.*" ,at = true)
    public void createGroup(GroupMsg msg, MsgSender sender){
        robotQQ= msg.getThisCode();
        String str=msg.getMsg();
        int atNum=searchAtNumber(str);
        String groupMasterQQ=null;
        String groupName=null;
        String gameName=null;
        String[] strings=msg.getMsg().split(" +");

        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        Group group=new Group();
        group.setCreateDate(df.format(date));
        group.setGroupid(msg.getGroupCode());

        //暂时不支持替别人建会
        if(strings.length>4){
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
            return;
        }
        try {
            gameName=strings[3];//会长名字
            if(atNum>2){
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
                return;
            }else if(atNum==1){//只有at了机器人
                groupMasterQQ=msg.getQQ();
                groupName=strings[2];//工会名字
                group.setGroupName(groupName);
                group.setGroupMasterQQ(groupMasterQQ);
                DB.Instance.creatGroup(group,gameName);
                teamMember teamMember = new teamMember(groupMasterQQ, true, null, strings[3]);
                DB.Instance.joinGroup(teamMember, group.getGroupid());
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), groupName + " 工会已经创建好辣");
            }
//            else {//有at其他人
//                groupMasterQQ=strings[4].substring(10,strings[4].indexOf("]"));
//                groupName=strings[2];//工会名字
//                group.setGroupName(groupName);
//                group.setGroupMasterQQ(groupMasterQQ);
//                gameName=null;
//                teamMember teamMember=new teamMember(msg.getQQCode(),false,null,strings[3]);
//                System.out.println(group); System.out.println(gameName);System.out.println(teamMember);
//                //DB.Instance.creatGroup(group,gameName,teamMember);
//            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
        }


    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#批量入会.*")
    public void getGroups(GroupMsg msg, MsgSender sender) {
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
            int[] sum = {0, 0, 0};//已有工会，成功入会,错误未加入

            ArrayList<String> have = new ArrayList<>();
            ArrayList<String> success = new ArrayList<>();
            ArrayList<String> error = new ArrayList<>();
            for (String s : strings) {
                teamMember teamMember = new teamMember(s.substring(10, s.length() - 1), false, null, pcrGroupMap.get(s));
                switch (DB.Instance.joinGroup(teamMember, msg.getGroupCode())) {
                    case -1:
                    case 3:
                    case 1:
                        sum[2]++;//错误
                        error.add(teamMember.getUserQQ());
                        break;
                    case 0:
                        sum[0]++;//已有工会
                        have.add(teamMember.getUserQQ());
                        break;
                    case 2:
                        sum[1]++;//成功加入
                        success.add(teamMember.getUserQQ());
                        break;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("本次操作，成功入会：").append(sum[1]).append("人");
            for (String s : success) {
                stringBuilder.append("[CQ:at,qq=").append(s).append("]");
            }
            stringBuilder.append("\n已有工会：").append(sum[0]).append("人");
            for (String s : have) {
                stringBuilder.append("[CQ:at,qq=").append(s).append("]");
            }
            stringBuilder.append("\n错误未加入：").append(sum[2]).append("人\n");
            for (String s : error) {
                stringBuilder.append("[CQ:at,qq=").append(s).append("]");
            }

            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
            return;
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#入会.*" ,at = true)
    public void getGroup(GroupMsg msg, MsgSender sender){
        teamMember teamMember = new teamMember(msg.getQQCode(), false, null, getVar(msg.getMsg()));
        switch (DB.Instance.joinGroup(teamMember,msg.getGroupCode())){
            case -1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), error);//错误
                break;
            case 0:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), isHaveGroup);//已有工会
                break;
            case 1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), isFullGroup);//工会满员
                break;
            case 2:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), successJoinGroup);//成功加入
                break;
            case 3:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), noThisGroup);//成功加入
                break;
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#退会.*", at = true)
    public void outGroup(GroupMsg msg, MsgSender sender){
        switch (DB.Instance.outGroup(msg.getQQCode())){
            case -1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), error);
                break;
            case 0:
                if (DB.Instance.isSuperPower(msg.getGroupCode(), msg.getQQCode())) {//会长则销毁工会
                    DB.Instance.dropGroup(msg.getGroupCode());
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), successDropGroup);
                    return;
                }
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), successOutGroup);
                break;
            case 1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), noGroupOutGroup);
                break;
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#转让会长.*")
    public void changeSuperPower(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (DB.Instance.isSuperPower(msg.getGroupCode(), msg.getQQCode()) && strings.size() > 0) {

            switch (DB.Instance.changeGroupMaster(msg.getQQCode(), strings.get(0).substring(10, strings.get(0).length() - 1))) {
                case -1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), noFindTheOne);
                    break;
                case 1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "她在别的工会哎，为什么会变成这样呢");
                    break;
                case 2:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), successChangeSuperPower);
                    break;
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有会长权限");
        }

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#撤下管理.*")
    public void downAdmin(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (DB.Instance.isSuperPower(msg.getGroupCode(), msg.getQQCode()) && strings.size() > 0) {

            switch (DB.Instance.changeGroupMaster(msg.getQQCode(), strings.get(0).substring(10, strings.get(0).length() - 1))) {
                case -1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), noFindTheOne);
                    break;
                case 1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "她在别的工会哎，为什么会变成这样呢");
                    break;
                case 2:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), successChangeSuperPower);
                    break;
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有会长权限");
        }

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#未出刀.*")
    public void searchVoidKnife(GroupMsg msg, MsgSender sender){
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        HashMap<String, Integer> map;
        StringBuilder src = new StringBuilder();
        if (strings != null && strings.size() > 0) {
            map = new HashMap<>();
            for (String QQ : strings) {
                map.putAll(DB.getInstance().searchVoidKnifeByGroup(QQ, 2));
            }
        } else {
            map = DB.getInstance().searchVoidKnifeByGroup(msg.getGroupCode(), 1);
        }
        Set<String> set = map.keySet();
        src = new StringBuilder("统计如下:\n");
        int flag;
        for (String s : set) {
            flag = map.get(s);
            if (flag > 0) {
                src.append("[CQ:at,qq=" + s + "] ,没出" + flag + "刀\n");
            }
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), src.toString());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "已出刀.*")
    public void searchKnife(GroupMsg msg, MsgSender sender) {
        List<Knife> list = new ArrayList<>();
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        StringBuilder stringBuilder = new StringBuilder();
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        if (strings != null && strings.size() > 0) {
            //找 人的出刀
            for (String s : strings) {
                System.out.println(s);
                list.addAll(DB.getInstance().searchKnife(s.substring(10, strings.get(0).length() - 1), null, df.format(date)));
            }

        } else {
            //找整个工会的出刀
            list = DB.getInstance().searchKnife(null, msg.getGroupCode(), df.format(date));
        }
        if (list != null) {
            stringBuilder.append("出刀信息：");
            for (Knife knife : list) {
                stringBuilder.append("\n-----\n编号: ").append(knife.getId());
                stringBuilder.append("\n昵称：").append(DB.Instance.searchName(knife.getKnifeQQ()));
                stringBuilder.append("\n扣扣：").append(knife.getKnifeQQ());
                stringBuilder.append("\n伤害：").append(knife.getHurt());
                stringBuilder.append("\n周目").append(knife.getNo() / 10);
                stringBuilder.append(",").append(knife.getNo() - (knife.getNo() / 10) * 10).append("王");
            }
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), stringBuilder.toString());
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "还没有刀信息哦");
        }

    }


    public static void AllCoolDown() {
        coolDown = new HashMap<>();
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#出刀"})
    public void getKnife(GroupMsg msg, MsgSender sender){
        FightStatue fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
        if(fightStatue!=null){
            switch (DB.getInstance().joinTree(msg.getQQCode())) {
                case -1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "好像出了点什么状况");
                    break;
                case 1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(),"¿,他群间谍发现，建议rbq一周" );
                    break;
                case 2:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "¿,打咩，还在出刀这么又出刀了");
                    break;
                case 3:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已出刀");
                    break;
            }
        }else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没有开启工会战惹");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#挂树")
    public void getTree(GroupMsg msg, MsgSender sender){
        switch (DB.Instance.trueTree(msg.getQQCode(),msg.getGroupCode())){
            case -1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), error);
                break;
            case 1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), noInDataBase);
                break;
            case 2:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), isTree);
                break;
            case 3:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), notInTree);
                break;
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#收刀.*", "#交刀.*"})
    public void outKnife(GroupMsg msg, MsgSender sender){
        StringBuilder stringBuilder=new StringBuilder();
        FightStatue fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
        if(fightStatue!=null) {//没有工会boss进度数据
            try {
                if (fightStatue.getStartTime().compareTo(new SimpleDateFormat(dateFormat).format(new Date())) <= 0) { //时间若已过开始则可以上报伤害
                    DB.Instance.hurtfight(msg.getQQCode(), getHurt(msg.getMsg(), 1), sender);
                    fightStatue = DB.Instance.searchFightStatue(msg.getQQCode());
                    stringBuilder.append("现在boss状态\n周目:").append(fightStatue.getLoop()).append("\n").append(fightStatue.getSerial()).append("王\n");
                    if (fightStatue.getRemnant() == -1) {
                        stringBuilder.append("血量：未录入");
                    } else {
                        stringBuilder.append("血量：").append(fightStatue.getRemnant());
                    }
                } else {
                    stringBuilder.append("会战即将开始，还没有到打boss的时候哦");
                }
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), commandError);
            }
        }else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没开始为什么就交刀惹");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#结束会战")
    public void endFight(GroupMsg msg, MsgSender sender) {
        int i = DB.Instance.endFight(msg.getGroupCode(), msg.getQQCode());
        switch (i) {
            case -1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), error);
                break;
            case 0://数据库离还没建这个工会或者没这权限
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
                break;
            case 1://成功结束
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), SuccessEndFight);
                break;
            case 2://会战没开启
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), EngFightStartDouble);
                break;
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#开始会战.*")
    public void startFight(GroupMsg msg, MsgSender sender) {
        String time = msg.getMsg().replaceAll(" +", "").substring(5);
        int i = 0;

        i = DB.Instance.startFight(msg.getGroupCode(), msg.getQQCode(), time);
        switch (i) {
            case -1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), error);
                break;
            case 0://数据库离还没建这个工会或者没这权限
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
                break;
            case 1://成功开始
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), SuccessStartFight);
                break;
            case 2://已经开始
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), StartFightStartDouble);
                break;
            case 3:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "日期格式错误，示例  20:05:12");
                break;
            case 4:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "成功预约，会战即将开始");
                break;
        }

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "up十连")
    public void Gashapon(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), dp_UpGashapon(10));
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "十连")
    public void Gashapon_(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), dp_Gashapon(10));
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "井")
    public void Gashapon__(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), dp_Gashapon(300));
            reFlashCoolDown(msg.getQQCode());
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "在冷却中");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "up井")
    public void Gashapon___(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), dp_UpGashapon(300));
            reFlashCoolDown(msg.getQQCode());
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "在冷却中");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "up抽卡.*")
    public void Gashapon____(GroupMsg msg, MsgSender sender) {
        String str = msg.getMsg().replaceAll(" +", "");
        int q = Integer.parseInt(str.substring(4));
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), dp_UpGashapon(q));
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "抽卡.*")
    public void Gashapon_____(GroupMsg msg, MsgSender sender) {

        String str = msg.getMsg().replaceAll(" +", "");
        int q = Integer.parseInt(str.substring(2));
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), dp_Gashapon(q));
    }

    public String dp_Gashapon(int num) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> jues = new ArrayList<>();
        for (int i = 0; i < num - num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else {
                tw++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();
        stringBuilder.append("一共抽出了").append(thre).append("个三星").append(tw).append("个两星").append(on).append("个一星\n三星角色:");
        for (int i = 0; i < thre; i++) {
            int j = random.nextInt(Three.length);
            if (map1.get(Three[j]) != null) {
                map1.put(Three[j], map1.get(Three[j]) + 1);
            } else {
                map1.put(Three[j], 1);
            }
        }
        for (int i = 0; i < tw; i++) {
            int j = random.nextInt(two.length);
            if (map2.get(two[j]) != null) {
                map2.put(two[j], map2.get(two[j]) + 1);
            } else {
                map2.put(two[j], 1);
            }
        }
        for (int i = 0; i < on; i++) {
            int j = random.nextInt(one.length);
            if (map3.get(one[j]) != null) {
                map3.put(one[j], map3.get(one[j]) + 1);
            } else {
                map3.put(one[j], 1);
            }
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        for (String s : set1) {
            stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
        }
        stringBuilder.append("\n二星角色有：");
        for (String s : set2) {
            stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
        }
        stringBuilder.append("\n一星角色有：");
        for (String s : set3) {
            stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
        }

        return stringBuilder.toString();
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = ".*老婆.*", at = true)
    public void kimo(GroupMsg msg, MsgSender sender) {
        Random random = new Random();
        random.setSeed(new Date().getTime());

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), kimo_Definde[random.nextInt(kimo_Definde.length)]);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "切噜.*")
    public void qielu(GroupMsg msg, MsgSender sender) {
        String needTran = msg.getMsg().replaceAll(" +", "");
        if (needTran.length() > 2) {
            needTran = needTran.substring(2);
            byte[] bytes = needTran.getBytes();
            StringBuilder tranled = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                int[] cache = spiltByte(bytes[i] < 0 ? -bytes[i] + 127 : bytes[i]);
                tranled.append(QieLU[cache[0]]);
                tranled.append(QieLU[cache[1]]);
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), tranled.toString());
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有要翻译的语句哎");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "翻译切噜.*")
    public void reqielu(GroupMsg msg, MsgSender sender) {
        String needTran = msg.getMsg().replaceAll(" +", "");
        needTran = needTran.replaceAll(",", "%%");
        needTran = needTran.replaceAll("扣", "扣扣");
        needTran = needTran.substring(4);
        if (needTran.length() > 0) {
            ArrayList<Byte> bytes = new ArrayList<Byte>();
            //防止前面和最后出现"，"这个不和谐因素
            char[] cache = new char[2];
            int q, w;
            for (int i = 0; i < needTran.length(); i += 4) {
                cache[0] = needTran.charAt(i + 2);
                cache[1] = needTran.charAt(i + 3);
                q = reQieLU.get(String.valueOf(cache));
                cache[0] = needTran.charAt(i);
                cache[1] = needTran.charAt(i + 1);
                w = reQieLU.get(String.valueOf(cache));
                bytes.add(respiltByte(q, w));
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), String.valueOf(getChars(bytes.toArray(new Byte[0]))));
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有要翻译的语句哎");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "改名.*")
    public void reName(GroupMsg msg, MsgSender sender) {
        String newName = msg.getMsg();
        newName = newName.replaceAll(" +", "");
        newName = newName.substring(2);

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), DB.Instance.changeName(msg.getQQCode(), newName));

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "改工会名.*")
    public void reGroupName(GroupMsg msg, MsgSender sender) {
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            String newName = msg.getMsg();
            newName = newName.replaceAll(" +", "");
            newName = newName.substring(4);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), DB.Instance.changeGroupName(msg.getGroupCode(), newName));
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "工会信息")
    public void GroupNameStatue(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), DB.Instance.groupStatue(msg.getGroupCode()));
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "工会成员列表")
    public void GroupMemberList(GroupMsg msg, MsgSender sender) {
        StringBuilder stringBuilder = new StringBuilder();
        List<teamMember> teamMembers = DB.Instance.groupMemberList(msg.getGroupCode());
        if (teamMembers != null) {
            stringBuilder.append("工会成员:\n");
            for (teamMember teamMember : teamMembers) {
                stringBuilder.append("qq：").append(teamMember.getUserQQ());
                stringBuilder.append("  昵称：").append(teamMember.getName());
                if (teamMember.isPower()) {
                    stringBuilder.append("  是管理员哦 \n");
                } else {
                    stringBuilder.append("\n");
                }
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
            return;
        } else {
            stringBuilder.append("还没有创建工会或者不在哦");
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
        }

    }

    //撤刀 撤回刀的编号
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "撤刀.*")
    public void dropKnife(GroupMsg msg, MsgSender sender) {
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            int id = Integer.valueOf(msg.getMsg().replaceAll(" +", "").substring(2));
            if (DB.Instance.deleteKnife(id)) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "操作成功");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    //调整boss状态 周目 几王 血量
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "调整boss状态.*")
    public void changeBoss(GroupMsg msg, MsgSender sender) {
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            String[] change = msg.getMsg().replaceAll(" +", " ").split(" ");

            boolean is = DB.Instance.changeBoss(msg.getGroupCode(), Integer.valueOf(change[1]), Integer.valueOf(change[2]), Integer.valueOf(change[3]));
            if (is) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "boss调整成功");
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "变更了周目，将还在出刀的人全部下树");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    //代刀 @代刀的那个人 伤害值
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "代刀.*")
    public void sideKnife(GroupMsg msg, MsgSender sender) {
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);

            DB.Instance.hurtfight(strings.get(0).substring(10, strings.get(0).length() - 1), getHurt(msg.getMsg(), 2), sender);
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    public String dp_UpGashapon(int num) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();


        for (int i = 0; i < num - num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else {
                tw++;
            }
        }
        stringBuilder.append("一共抽出了").append(thre).append("个三星").append(tw).append("个两星").append(on).append("个一星\n三星角色:");
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();
        stringBuilder.append("一共抽出了").append(thre).append("个三星").append(tw).append("个两星").append(on).append("个一星\n三星角色:");
        for (int i = 0; i < thre; i++) {
            int j = random.nextInt(Three.length);
            if (map1.get(Three[j]) != null) {
                map1.put(Three[j], map1.get(Three[j]) + 1);
            } else {
                map1.put(Three[j], 1);
            }
        }
        for (int i = 0; i < tw; i++) {
            int j = random.nextInt(two.length);
            if (map2.get(two[j]) != null) {
                map2.put(two[j], map2.get(two[j]) + 1);
            } else {
                map2.put(two[j], 1);
            }
        }
        for (int i = 0; i < on; i++) {
            int j = random.nextInt(one.length);
            if (map3.get(one[j]) != null) {
                map3.put(one[j], map3.get(one[j]) + 1);
            } else {
                map3.put(one[j], 1);
            }
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        for (String s : set1) {
            stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
        }
        stringBuilder.append("\n二星角色有：");
        for (String s : set2) {
            stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
        }
        stringBuilder.append("\n一星角色有：");
        for (String s : set3) {
            stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
        }
        return stringBuilder.toString();
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"设置管理.*", "设置管理员.*", "添加管理员.*", "添加管理.*"})
    public void setAdmin(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            switch (DB.Instance.setAdmin(strings.get(0).substring(10, strings.get(0).length() - 1), msg.getGroupCode())) {
                case 0:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), noFindTheOne);
                    break;
                default:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "成功设置管理员");
                    break;

            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "踢人.*")
    public void kickman(GroupMsg msg, MsgSender sender) {
        int num;
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
            num = DB.getInstance().deleteMember(strings.get(0).substring(10, strings.get(0).length() - 1));
            if (num == 0) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有踢掉任何一个人，@的这个人是不是还没有加入这个工会呢");
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "成功踢掉了");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "查树")
    public void searchTree(GroupMsg msg, MsgSender sender) {
        List<String> trees = DB.Instance.searchTree(msg.getGroupCode());

        if (trees.size() == 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "无 人 挂 树");
        } else {
            StringBuilder stringBuilder = new StringBuilder("挂树名单:");
            for (String QQ : trees) {
                stringBuilder.append("\n[CQ:at,qq=").append(QQ).append("] ").append(DB.Instance.searchName(QQ));
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
        }
    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "正在出刀")
    public void searchOutKnife(GroupMsg msg, MsgSender sender) {
        List<String> trees = DB.Instance.searchOutKnife(msg.getGroupCode());

        if (trees.size() == 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "无 人 出 刀");
        } else {
            StringBuilder stringBuilder = new StringBuilder("出刀名单:");
            for (String QQ : trees) {
                stringBuilder.append("\n[CQ:at,qq=").append(QQ).append("] ").append(DB.Instance.searchName(QQ));
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"boss状态", "boss", "boss咋样了", "boss还好吗"})
    public void getBoss(GroupMsg msg, MsgSender sender) {
        FightStatue fightStatue = DB.Instance.getFightStatue(msg.getGroupCode());
        if (fightStatue != null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "现在为 " + fightStatue.getLoop() + "周目\n" + fightStatue.getSerial() + "王\n剩余血量："
                    + fightStatue.getRemnant() + "");
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notBossOrNotDate);
        }
    }

    /**
     * 刷新抽卡冷却时间
     */
    public void reFlashCoolDown(String QQ) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusMinutes(2);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = localDateTime.format(dateTimeFormatter);
        if (coolDown == null) {
            coolDown = new HashMap<>();
            coolDown.put(QQ, time);
        } else {
            coolDown.put(QQ, time);
        }
    }

    /**
     * 获取冷却时间是不是到了
     *
     * @param QQ
     * @return
     */
    public boolean isCool(String QQ) {
        if (coolDown == null) {
            coolDown = new HashMap<>();
            return true;
        } else {
            if (coolDown.get(QQ) != null) {
                return coolDown.get(QQ).compareTo(new SimpleDateFormat("HH:mm").format(new Date())) < 0;
            } else {
                return true;
            }
        }
    }

}