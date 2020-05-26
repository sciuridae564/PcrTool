package cn.sciuridae.CoolQ.Listener;

import cn.sciuridae.DB.bean.*;
import cn.sciuridae.DB.sqLite.DB;
import cn.sciuridae.Excel.excelWrite;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.QQCodeAble;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.sciuridae.CoolQ.Listener.prcnessIntercept.On;
import static cn.sciuridae.Tools.stringTool.*;
import static cn.sciuridae.constant.*;

@Service
public class prcnessListener {

    private static HashMap<String, String> coolDown;//抽卡冷却时间

    public static HashMap<String, List<String>> powerList;//管理列表

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#帮助.*", at = true)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), helpMsg);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = "#帮助")
    public void testListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, helpMsg);
    }

    /**
     * 格式 #建会@机器人 工会名 自己的游戏名字（没有@工会长默认创建者为工会长）
     *
     * @param msg
     * @param sender
     */
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#建会.*", at = true)
    public void createGroup(GroupMsg msg, MsgSender sender) {
        robotQQ = msg.getThisCode();
        String str = msg.getMsg();
        int atNum = searchAtNumber(str);
        String groupMasterQQ = null;
        String groupName = null;
        String groupMasterName = null;
        String[] strings = msg.getMsg().split(" +");

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        Group group = new Group();
        group.setCreateDate(df.format(date));
        group.setGroupid(msg.getGroupCode());

        //暂时不支持替别人建会
        if (strings.length > 4) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
            return;
        }
        //DB.Instance.searchGroupIdByGroupQQ(msg.getGroupCode());
        try {
            if (atNum > 2) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
                return;
            } else if (atNum == 1) {//只有at了机器人
                groupMasterQQ = msg.getQQ();
                groupName = strings[2];//工会名字
                groupMasterName = strings[3];
                group.setGroupName(groupName);
                group.setGroupMasterQQ(groupMasterQQ);
                int flag = DB.Instance.creatGroup(group, groupMasterName);
                if (flag == 0) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), groupMasterName + isHaveGroup);
                } else if (flag == 1) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), groupName + " 工会已经创建好辣");
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), error);
                }

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
        } catch (IndexOutOfBoundsException e) {
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
    @Filter(value = "#入会.*", at = true)
    public void getGroup(GroupMsg msg, MsgSender sender) {
        teamMember teamMember = new teamMember(msg.getQQCode(), false, null, getVar(msg.getMsg()));
        switch (DB.Instance.joinGroup(teamMember, msg.getGroupCode())) {
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
    public void outGroup(GroupMsg msg, MsgSender sender) {
        switch (DB.Instance.outGroup(msg.getQQCode())) {
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
            int i = DB.Instance.changeGroupMaster(msg.getQQCode(), strings.get(0).substring(10, strings.get(0).length() - 1));

            switch (i) {
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

            switch (DB.Instance.downAdmin(msg.getQQCode(), strings.get(0).substring(10, strings.get(0).length() - 1))) {
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
    @Filter(value = "未出刀.*")
    public void searchVoidKnife(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        HashMap<String, Integer> map;
        StringBuilder src;
        src = new StringBuilder();
        if (strings != null && strings.size() > 0) {
            map = new HashMap<>();
            for (String QQ : strings) {
                map.putAll(DB.getInstance().searchVoidKnifeByGroup(QQ, 2));
            }
        } else {
            map = DB.getInstance().searchVoidKnifeByGroup(msg.getGroupCode(), 1);
        }
        if (map == null) {//没有空刀信息
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notBossOrNotDate);
        } else {
            Set<String> set = map.keySet();
            src = new StringBuilder("统计如下:\n");
            int flag;
            for (String s : set) {
                System.out.println(s);
                flag = map.get(s);
                if (flag > 0) {
                    src.append("[CQ:at,qq=" + s + "] ,没出" + flag + "刀\n");
                }
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), src.toString());
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "已出刀.*")
    public void searchKnife(GroupMsg msg, MsgSender sender) {
        List<Knife> list = new ArrayList<>();
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        StringBuilder stringBuilder = new StringBuilder();
        if (strings != null && strings.size() > 0) {
            //找 人的出刀
            for (String s : strings) {
                list.addAll(DB.getInstance().searchKnife(s.substring(10, strings.get(0).length() - 1), null, getDate()));
            }

        } else {
            //找整个工会的出刀
            list = DB.getInstance().searchKnife(null, msg.getGroupCode(), getDate());
        }
        if (list.size() > 0) {
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
    public void getKnife(GroupMsg msg, MsgSender sender) {
        FightStatue fightStatue = DB.Instance.searchFightStatue(msg.getQQCode());
        if (fightStatue != null) {
            switch (DB.getInstance().joinTree(msg.getQQCode())) {
                case -1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "好像出了点什么状况");
                    break;
                case 1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "¿,他群间谍发现，建议rbq一周");
                    break;
                case 2:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "¿,打咩，还在出刀这么又出刀了");
                    break;
                case 3:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已出刀");
                    break;
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没有开启工会战惹");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#挂树")
    public void getTree(GroupMsg msg, MsgSender sender) {
        switch (DB.Instance.trueTree(msg.getQQCode(), msg.getGroupCode())) {
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
    public void outKnife(GroupMsg msg, MsgSender sender) {
        StringBuilder stringBuilder = new StringBuilder();
        FightStatue fightStatue = DB.Instance.searchFightStatue(msg.getQQCode());
        if (fightStatue != null) {//没有工会boss进度数据
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
        } else {
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
    @Filter(value = "#up十连")
    public void Gashapon(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_UpGashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.data);
            if (gashapon.ban) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#十连")
    public void Gashapon_(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_Gashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.data);
            if (gashapon.ban) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#井")
    public void Gashapon__(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_Gashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.data);
            if (gashapon.ban) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up井")
    public void Gashapon___(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_UpGashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.data);
            if (gashapon.ban) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up抽卡.*")
    public void Gashapon____(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            String str = msg.getMsg().replaceAll(" +", "");
            try {
                int q = Integer.parseInt(str.substring(5));
                if (q <= pricnessConfig.getGashaponMax() && isCool(msg.getQQCode())) {
                    Gashapon gashapon = dp_UpGashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.data);
                    if (gashapon.ban) {
                        sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
                    }
                    reFlashCoolDown(msg.getQQCode());
                } else {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡数额过大，no so much money");
                }
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "数字解析错误");
            }
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#抽卡.*")
    public void Gashapon_____(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            String str = msg.getMsg().replaceAll(" +", "");
            try {
                int q = Integer.parseInt(str.substring(3));
                if (q <= pricnessConfig.getGashaponMax() && isCool(msg.getQQCode())) {
                    Gashapon gashapon = dp_Gashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.data);
                    if (gashapon.ban) {
                        sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
                    }
                    reFlashCoolDown(msg.getQQCode());
                } else {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡数额过大，no so much money");
                }
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "数字解析错误");
            }
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }

    }

    /**
     * 普通池的概率
     *
     * @param num
     * @return
     */
    public Gashapon dp_Gashapon(int num) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个
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
        Gashapon g = new Gashapon();
        g.setData(get_GashaponString(on, tw, thre, map1, map2, map3));
        try {
            g.setBan(num / thre < 20);
        } catch (ArithmeticException e) {
            g.setBan(false);
        }
        return g;
    }

    /**
     * up池的概率
     *
     * @param num
     * @return
     */
    public Gashapon dp_UpGashapon(int num) {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0;//抽出来的三心二心有几个

        //无保底
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
        //有保底
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

        for (int i = 0; i < thre; i++) {
            int q = random.nextInt(25);
            if (q < 7) {//抽不抽的出来亚里沙
                if (map1.get(Three_plus[0]) != null) {
                    map1.put(Three_plus[0], map1.get(Three_plus[0]) + 1);
                } else {
                    map1.put(Three_plus[0], 1);
                }
            } else {
                int j = random.nextInt(Three.length);
                if (map1.get(Three[j]) != null) {
                    map1.put(Three[j], map1.get(Three[j]) + 1);
                } else {
                    map1.put(Three[j], 1);
                }
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
        Gashapon g = new Gashapon();
        g.setData(get_GashaponString(on, tw, thre, map1, map2, map3));
        try {
            g.setBan(num / thre < 20);
        } catch (ArithmeticException e) {
            g.setBan(false);
        }
        return g;
    }

    /**
     * 组织抽卡结果
     */
    public String get_GashaponString(int on, int tw, int thre, HashMap<String, Integer> map1, HashMap<String, Integer> map2, HashMap<String, Integer> map3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("一共抽出了");
        if (thre != 0) {
            stringBuilder.append(thre).append("个三星");
        }
        if (tw != 0) {
            stringBuilder.append(tw).append("个二星");
        }
        if (on != 0) {
            stringBuilder.append(on).append("个一星");
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        if (thre != 0) {
            stringBuilder.append("\n三星角色有：");
            for (String s : set1) {
                stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
            }
        }
        if (tw != 0) {
            stringBuilder.append("\n二星角色有：");
            for (String s : set2) {
                stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
            }
        }

        if (on != 0) {
            stringBuilder.append("\n一星角色有：");
            for (String s : set3) {
                stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
            }
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
            StringBuilder tranled = new StringBuilder();

            byte[] bytes = new byte[0];
            try {
                bytes = needTran.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
    @Filter(value = "#改名.*")
    public void reName(GroupMsg msg, MsgSender sender) {
        String newName = msg.getMsg();
        newName = newName.replaceAll(" +", "");
        newName = newName.substring(3);

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), DB.Instance.changeName(msg.getQQCode(), newName));

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#改工会名.*")
    public void reGroupName(GroupMsg msg, MsgSender sender) {
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            String newName = msg.getMsg();
            newName = newName.replaceAll(" +", "");
            newName = newName.substring(5);
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


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"设置管理.*", "设置管理员.*", "添加管理员.*", "添加管理.*"})
    public void setAdmin(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (DB.Instance.isSuperPower(msg.getGroupCode(), msg.getQQCode())) {
            switch (DB.Instance.setAdmin(strings.get(0).substring(10, strings.get(0).length() - 1))) {
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
        int num = 0;
        if (DB.Instance.powerCheck(msg.getQQCode(), msg.getGroupCode())) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
            String deleteQQ = strings.get(0).substring(10, strings.get(0).length() - 1);//要被踢掉的那个人
            if (DB.Instance.isSuperPower(msg.getGroupCode(), deleteQQ)) {
                num = -2;
            } else {
                num = DB.getInstance().deleteMember(deleteQQ);
            }
            if (num == 0) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有踢掉任何一个人，@的这个人是不是还没有加入这个工会呢");
            } else if (num == -2) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "堂下何人竟敢状告本官");
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
        localDateTime.plusSeconds(pricnessConfig.getGashaponcool());
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

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"生成excel.*"})
    public void getExcel(GroupMsg msg, MsgSender sender) {
        String processTime = msg.getMsg().substring(7).replaceAll(" +", "");
        Date time;
        if (processTime.equals("")) {
            time = new Date();
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            try {
                time = simpleDateFormat.parse(processTime);
            } catch (ParseException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "日期格式错误");
                return;
            }
        }
        ArrayList<Date> timelist = new ArrayList<>();
        timelist.add(time);

        int id = DB.Instance.searchGroupIdByGroupQQ(msg.getGroupCode());
        if (id != -1) {
            try {
                String groupQQ = msg.getGroupCode();//工会qq
                File file = new File(getExcelFileName(groupQQ, time));
                excelWrite excelWrite = new excelWrite(file, timelist, id);
                excelWrite.writedDate();
                excelWrite.reflashFile();
            } catch (NullPointerException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "这一天，还没有工会战的出刀");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), noThisGroup);
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"获取码"})
    public void getToken(PrivateMsg msg, MsgSender sender) {
        String QQ = msg.getQQCode();

        sender.SENDER.sendPrivateMsg(QQ, "你的码是：" + DB.Instance.getToken(QQ));
        sender.SENDER.sendPrivateMsg(QQ, "会战后台网址：http://" + ip + "/8080");

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭扭蛋"})
    public void openEgg(GroupMsg msg, MsgSender sender) {
        try {
            if (powerList.get(msg.getGroupCode()).contains(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setEggon(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭扭蛋");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            setjson();
            groupPower groupPower = new groupPower();
            groupPower.setEggon(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭扭蛋");
        }

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启扭蛋"})
    public void shutEgg(GroupMsg msg, MsgSender sender) {
        try {
            if (powerList.get(msg.getGroupCode()).contains(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setEggon(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            setjson();
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭PcrTool"})
    public void shut(GroupMsg msg, MsgSender sender) {
        try {
            if (powerList.get(msg.getGroupCode()).contains(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭PcrTool");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            setjson();
            groupPower groupPower = new groupPower();
            groupPower.setOn(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启PcrTool"})
    public void open(GroupMsg msg, MsgSender sender) {
        try {
            if (powerList.get(msg.getGroupCode()).contains(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            setjson();
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭提醒买药小助手"})
    public void shutbuy(GroupMsg msg, MsgSender sender) {
        try {
            if (powerList.get(msg.getGroupCode()).contains(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setButon(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            setjson();
            groupPower groupPower = new groupPower();
            groupPower.setButon(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启提醒买药小助手"})
    public void openbuy(GroupMsg msg, MsgSender sender) {
        try {
            if (powerList.get(msg.getGroupCode()).contains(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setButon(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            setjson();
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
        }
    }


    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"重载设置"})
    public void reloadconfig(PrivateMsg msg, MsgSender sender) {
        getconfig();
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "现在设置为：\n" +
                "提醒买药小助手图片名:" + pricnessConfig.getTixingmaiyao() +
                "\n抽卡上限" + pricnessConfig.getGashaponMax() +
                "\n抽卡冷却秒:" + pricnessConfig.getGashaponcool());
    }


    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"通用设置"})
    public void config(PrivateMsg msg, MsgSender sender) {
        getconfig();
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "现在设置为：\n" +
                "提醒买药小助手图片名:" + pricnessConfig.getTixingmaiyao() +
                "\n抽卡上限" + pricnessConfig.getGashaponMax() +
                "\n抽卡冷却秒:" + pricnessConfig.getGashaponcool());
    }
}