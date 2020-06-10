package cn.sciuridae.listener;

import cn.sciuridae.utils.bean.Gashapon;
import cn.sciuridae.utils.bean.groupPower;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.messages.types.PowerType;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.listener.prcnessIntercept.On;
import static cn.sciuridae.utils.ImageUtil.composeImg;
import static cn.sciuridae.utils.stringTool.*;

@Service
public class OtherListener {


    private static HashMap<String, String> coolDown;//抽卡冷却时间

    public static void AllCoolDown() {
        coolDown = new HashMap<>();
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#帮助", at = true, keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), helpMsg);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"帮助", "#帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void testListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, helpMsg);
    }

    //---------------------
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"其他功能帮助", "#其他功能帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void otherhelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, OTHER_HELP_MSG);
    }


    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"会战帮助", "#会战帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void fighthelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, FIGHT_HELP_MSG);
    }

    //------------------------------
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"其他功能帮助", "#其他功能帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void otherhelpListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, OTHER_HELP_MSG);
    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"会战帮助", "#会战帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void fighthelpListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, FIGHT_HELP_MSG);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"工会帮助", "#工会帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void grouphelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, GROUP_HELP_MSG);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"工会帮助", "#工会帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void grouphelpListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, GROUP_HELP_MSG);
    }






    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up十连", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_UpGashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.getData());
            if (gashapon.isBan()) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#十连", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon_(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_Gashapon(10);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.getData());
            if (gashapon.isBan()) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#井", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon__(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_Gashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.getData());
            if (gashapon.isBan()) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up井", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void Gashapon___(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            Gashapon gashapon = dp_UpGashapon(300);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.getData());
            if (gashapon.isBan()) {
                sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
            }
            reFlashCoolDown(msg.getQQCode());
        } else {
            //发送冷却提示消息
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡抽的那么快，人家会受不了的");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#up抽卡", keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void Gashapon____(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            String str = msg.getMsg().replaceAll(" +", "");
            try {
                int q = Integer.parseInt(str.substring(5));
                //抽卡次数不能为1以下
                if (q < 1) {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡数额过小");
                    return;
                }
                if (q <= pricnessConfig.getGashaponMax() && isCool(msg.getQQCode())) {
                    Gashapon gashapon = dp_UpGashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.getData());
                    if (gashapon.isBan()) {
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
    @Filter(value = "#抽卡", keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void Gashapon_____(GroupMsg msg, MsgSender sender) {
        if (isCool(msg.getQQCode())) {
            String str = msg.getMsg().replaceAll(" +", "");
            try {
                int q = Integer.parseInt(str.substring(3));//获取抽多少次
                //抽卡次数不能为1以下
                if (q < 1) {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡数额过小");
                    return;
                }
                //抽卡次数不能超过设置的最高值and冷却时间到没到
                if (q <= pricnessConfig.getGashaponMax() && isCool(msg.getQQCode())) {
                    Gashapon gashapon = dp_Gashapon(q);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), coolQAt + msg.getQQCode() + "]" + gashapon.getData());
                    //抽卡太欧需要被禁言
                    if (gashapon.isBan()) {
                        sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1);
                    }
                    reFlashCoolDown(msg.getQQCode());

                } else {
                    sender.SENDER.sendPrivateMsg(msg.getQQCode(), "抽卡数额过大");
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
            if (j > 1000 - baijinThreeChance) {
                thre++;
            } else if (j > baijiOneChance) {
                tw++;
            } else {
                on++;
            }
        }
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - baijinThreeChance) {
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
            map1.merge(Three[j], 1, (a, b) -> a + b);
        }
        for (int i = 0; i < tw; i++) {
            int j = random.nextInt(two.length);
            map2.merge(two[j], 1, (a, b) -> a + b);
        }
        for (int i = 0; i < on; i++) {
            int j = random.nextInt(one.length);
            map3.merge(one[j], 1, (a, b) -> a + b);
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
            if (j > 1000 - ThreeChance) {
                thre++;
            } else if (j > 1000 - ThreeChance - TwoChance) {
                tw++;
            } else {
                on++;
            }
        }
        //有保底
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 1000 - ThreeChance) {
                thre++;
            } else {
                tw++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();

        for (int i = 0; i < thre; i++) {
            int q = random.nextInt(ThreeChance);
            if (q < upThreeChance) {
                //抽出来up角色
                map1.merge(Three_plus[q % Three_plus.length], 1, (a, b) -> a + b);
            } else {
                int j = random.nextInt(noUpThree.length);
                map1.merge(noUpThree[j], 1, (a, b) -> a + b);
            }
        }
        for (int i = 0; i < tw; i++) {
            int q = random.nextInt(TwoChance);
            if (q < upTwoChance) {
                //抽出来up角色
                map2.merge(two_plus[q % two_plus.length], 1, (a, b) -> a + b);
            } else {
                int j = random.nextInt(noUptwo.length);
                map2.merge(noUptwo[j], 1, (a, b) -> a + b);
            }
        }
        for (int i = 0; i < on; i++) {
            int q = random.nextInt(OneChance);
            if (q < upOneChance) {
                //抽出来up角色
                try {
                    map3.merge(one_plus[q % one_plus.length], 1, (a, b) -> a + b);
                } catch (IndexOutOfBoundsException e) {
                }
            } else {
                int j = random.nextInt(noUpone.length);
                map3.merge(noUpone[j], 1, (a, b) -> a + b);
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
    private String get_GashaponString(int on, int tw, int thre, HashMap<String, Integer> map1, HashMap<String, Integer> map2, HashMap<String, Integer> map3) {
        StringBuilder stringBuilder = new StringBuilder();
        if (thre != 0) {
            stringBuilder.append("★★★×").append(thre);
        }
        if (tw != 0) {
            stringBuilder.append("★★×").append(tw);
        }
        if (on != 0) {
            stringBuilder.append("★×").append(on);
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        ArrayList<String> list = new ArrayList<>();
        int i = 0;
        for (String s : set1) {
            int j = map1.get(s);
            for (i = 0; i < j; i++) {
                list.add(s);
            }
        }

        //人物图片
        if (canSendImage) {
            try {
                String s = composeImg(list);
                if (null != s) {
                    File file = new File(s);
                    CQCode cqCode_image = CQCodeUtil.build().getCQCode_Image("file://" + file.getAbsolutePath());
                    stringBuilder.append(cqCode_image.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (thre != 0) {
            stringBuilder.append("\n三星：");
            for (String s : set1) {
                stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
            }
        }
        if (tw != 0) {
            stringBuilder.append("\n二星：");
            for (String s : set2) {
                stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
            }
        }

        if (on != 0) {
            stringBuilder.append("\n一星：");
            for (String s : set3) {
                stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
            }
        }

        return stringBuilder.toString();
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "老婆", at = true, keywordMatchType = KeywordMatchType.CONTAINS)
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
    @Filter(value = {"#关闭扭蛋"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void openEgg(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setEggon(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭扭蛋");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            groupPower.setEggon(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭扭蛋");
            setjson();
        }

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启扭蛋"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shutEgg(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setEggon(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
            setjson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭PcrTool"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shut(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭PcrTool");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            groupPower.setOn(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭PcrTool");
            setjson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启PcrTool"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void open(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
            setjson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭提醒买药小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shutbuy(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setButon(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意

            groupPower groupPower = new groupPower();
            groupPower.setButon(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
            setjson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启提醒买药小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void openbuy(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getPowerType();
            if (powerType.isAdmin() || powerType.isOwner() || pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setButon(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
            setjson();
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"重载设置"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void reloadconfig(PrivateMsg msg, MsgSender sender) {
        getconfig();
        getgachi();
        getEvent();
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "扭蛋池，马事件已更新,现在设置为：\n" +
                "提醒买药小助手图片名:" + pricnessConfig.getTixingmaiyao() +
                "\n抽卡上限" + pricnessConfig.getGashaponMax() +
                "\n抽卡冷却秒:" + pricnessConfig.getGashaponcool() +
                "\n总开关默认：" + pricnessConfig.isPcrToonon() +
                "\n好像没啥用的开关默认：" + pricnessConfig.isButon() +
                "\n扭蛋开关默认：" + pricnessConfig.isEggon() +
                "\n赛马开关默认：" + pricnessConfig.isHorse() +
                "\nmasterQQ：" + pricnessConfig.getMasterQQ());
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"通用设置"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void config(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "现在设置为：\n" +
                "提醒买药小助手图片名:" + pricnessConfig.getTixingmaiyao() +
                "\n抽卡上限" + pricnessConfig.getGashaponMax() +
                "\n抽卡冷却秒:" + pricnessConfig.getGashaponcool() +
                "\n总开关默认：" + pricnessConfig.isPcrToonon() +
                "\n好像没啥用的开关默认：" + pricnessConfig.isButon() +
                "\n扭蛋开关默认：" + pricnessConfig.isEggon() +
                "\n赛马开关默认：" + pricnessConfig.isHorse() +
                "\nmasterQQ：" + pricnessConfig.getMasterQQ());
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#查看本群设置"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void groupconfig(GroupMsg msg, MsgSender sender) {
        groupPower groupPower = On.get(msg.getGroupCode());
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "现在设置为：\n" +
                "扭蛋开关:" + groupPower.isEggon() +
                "\n买药小助手开关" + groupPower.isButon() +
                "\n买马开关：" + groupPower.isHorse());
    }



    /**
     * 刷新抽卡冷却时间
     */
    public void reFlashCoolDown(String QQ) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusSeconds(pricnessConfig.getGashaponcool());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = localDateTime.format(dateTimeFormatter);

        if (coolDown == null) {
            System.out.println("aaa");
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
                return coolDown.get(QQ).compareTo(new SimpleDateFormat("HH:mm:ss").format(new Date())) < 0;
            } else {
                return true;
            }
        }
    }
}
