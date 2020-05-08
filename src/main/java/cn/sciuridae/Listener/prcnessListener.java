package cn.sciuridae.Listener;

import cn.sciuridae.bean.FightStatue;
import cn.sciuridae.bean.Group;
import cn.sciuridae.bean.Knife;
import cn.sciuridae.bean.teamMember;
import cn.sciuridae.sqLite.DB;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.forte.qqrobot.utils.CQUtils;
import com.simplerobot.modules.utils.KQCode;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import sun.reflect.generics.tree.Tree;
import sun.util.resources.cldr.haw.CalendarData_haw_US;

import javax.transaction.TransactionRequiredException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.sciuridae.Tools.stringTool.*;
import static cn.sciuridae.constant.*;

@Beans
public class prcnessListener {

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
    @Filter(value = "#批量入会.*", at = true)
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
            stringBuilder.append("本次操作，成功入会：").append(sum[1]).append("人\n");
            for (String s : success) {
                stringBuilder.append("CQ:at.qq=").append(s).append("]");
            }
            stringBuilder.append("已有工会：").append(sum[0]).append("人\n");
            for (String s : have) {
                stringBuilder.append("CQ:at.qq=").append(s).append("]");
            }
            stringBuilder.append("错误未加入：").append(sum[2]).append("人\n");
            for (String s : error) {
                stringBuilder.append("CQ:at.qq=").append(s).append("]");
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
                if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {//会长则销毁工会
                    DB.Instance.dropGroup(msg.getGroupCode());
                }
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), successOutGroup);
                break;
            case 1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), noGroupOutGroup);
                break;
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#查刀.*"  )
    public void searchVoidKnife(GroupMsg msg, MsgSender sender){

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), DB.getInstance().searchVoidKnife(msg.getQQCode()));
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#出刀.*" )
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
    @Filter(value = "#上树.*" )
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
    @Filter(value = "#收刀.*")
    public void outKnife(GroupMsg msg, MsgSender sender){
        String date=new SimpleDateFormat(dateFormat).format(new Date());
        StringBuilder stringBuilder=new StringBuilder();
        FightStatue fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
        if(fightStatue!=null) {//没有工会boss进度数据
            int no = fightStatue.getLoop() * 10 + fightStatue.getSerial();
            try {

                 DB.Instance.hurtfight(msg.getQQCode(),getHurt(msg.getMsg()),sender);
                fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
                stringBuilder.append("现在boss状态\n周目:").append(fightStatue.getLoop()).append("\n").append(fightStatue.getSerial()).append("王\n");
                if(fightStatue.getRemnant()==-1){
                    stringBuilder.append("血量：未录入");
                }else {
                    stringBuilder.append("血量：").append(fightStatue.getRemnant());
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
    @Filter(value = "#开始会战.*")
    public void startFight(GroupMsg msg, MsgSender sender) {
        int i = DB.Instance.startFight(msg.getGroupCode(), msg.getQQCode());
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
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#结束会战.*")
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
    @Filter(value = "#查看工会成员.*")
    public void searchGroupMember(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没做");
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "签到")
    public void signIn(GroupMsg msg, MsgSender sender) {
        System.out.println("没做");
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "up十连.*")
    public void Gashapon(GroupMsg msg, MsgSender sender) {

        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> jues = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < 1; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else {
                tw++;
            }
        }
        stringBuilder.append("一共抽出了").append(thre).append("个三星").append(tw).append("个两星").append(on).append("个一星\n三星角色:");
        for (int i = 0; i < thre; i++) {
            int j = random.nextInt(50);
            if (j < 36) {
                stringBuilder.append(Three[j / 3]).append(",");
            } else if (j < 43) {
                stringBuilder.append(Three_plus[0]).append(",");
            } else {
                stringBuilder.append(Three_plus[1]).append(",");
            }
        }
        stringBuilder.append("\n两星角色:");
        for (int i = 0; i < tw; i++) {
            int j = random.nextInt(52);
            if (j < 36) {
                stringBuilder.append(two[j / 3]).append(",");
            } else {
                stringBuilder.append(two_plus[(j - 36) / 4]).append(",");
            }
        }
        stringBuilder.append("\n一星角色:");
        for (int i = 0; i < on; i++) {
            int j = random.nextInt(795);
            if (j < 695) {
                stringBuilder.append(one[j / 77]).append(",");
            } else {
                stringBuilder.append(one_plus[0]).append(",");
            }
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "十连.*")
    public void Gashapon_(GroupMsg msg, MsgSender sender) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> jues = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < 1; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else {
                tw++;
            }
        }
        stringBuilder.append("一共抽出了").append(thre).append("个三星").append(tw).append("个两星").append(on).append("个一星\n三星角色:");
        for (int i = 0; i < thre; i++) {
            int j = random.nextInt(Three.length);
            stringBuilder.append(Three[j]).append(",");
        }
        stringBuilder.append("\n两星角色:");
        for (int i = 0; i < tw; i++) {
            int j = random.nextInt(two.length);
            stringBuilder.append(two[j]).append(",");
        }
        stringBuilder.append("\n一星角色:");
        for (int i = 0; i < on; i++) {
            int j = random.nextInt(one.length);
            stringBuilder.append(one[j]).append(",");
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "井")
    public void Gashapon__(GroupMsg msg, MsgSender sender) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> jues = new ArrayList<>();
        int q = 300;
        for (int i = 0; i < q - q / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < q / 10; i++) {
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

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "up井")
    public void Gashapon___(GroupMsg msg, MsgSender sender) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> jues = new ArrayList<>();

        int q = 300;

        for (int i = 0; i < q - q / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < q / 10; i++) {
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
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "up抽卡")
    public void Gashapon____(GroupMsg msg, MsgSender sender) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> jues = new ArrayList<>();
        String str = msg.getMsg().replaceAll(" +", "");
        int q = Integer.parseInt(str.substring(4));

        for (int i = 0; i < q - q / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < q / 10; i++) {
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
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "抽卡")
    public void Gashapon_____(GroupMsg msg, MsgSender sender) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> jues = new ArrayList<>();
        String str = msg.getMsg().replaceAll(" +", "");
        int q = Integer.parseInt(str.substring(4));
        for (int i = 0; i < q - q / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < q / 10; i++) {
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

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
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
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), String.valueOf(getChars(bytes.toArray(new Byte[bytes.size()]))));
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有要翻译的语句哎");
        }
    }
}