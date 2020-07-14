package cn.sciuridae.listener;

import cn.sciuridae.dataBase.service.qqGroupService;
import cn.sciuridae.utils.ImageUtil;
import cn.sciuridae.utils.bean.Gashapon;
import cn.sciuridae.utils.bean.groupPower;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Ignore;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberIncrease;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.listener.Intercept.prcnessIntercept.On;
import static cn.sciuridae.utils.ImageUtil.composeImg;
import static cn.sciuridae.utils.stringTool.*;

@Service
public class OtherListener {
    @Autowired
    qqGroupService qqGroupServiceImpl;

    private static HashMap<String, LocalDateTime> coolDown;//抽卡冷却时间

    public static void AllCoolDown() {
        coolDown = new HashMap<>();
    }

    //加群提醒
    @Listen(MsgGetTypes.groupMemberIncrease)
    public void groupWelcome(GroupMemberIncrease msg, MsgSender sender) {
        if (qqGroupServiceImpl.isGroupWelcomOn(msg.getGroupCodeNumber())) {
            String groupWelcom = qqGroupServiceImpl.getGroupWelcom(msg.getGroupCodeNumber());
            groupWelcom=groupWelcom.replaceAll("\\{bot_name}",sender.bot().getInfo().getName());
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), groupWelcom);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#帮助", at = true, keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), helpMsg);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"机器人设置", "#机器人设置", "4"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void testListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, CONFIG_MES);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#机器人设置"}, at = true, keywordMatchType = KeywordMatchType.STARTS_WITH)
    public void configListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), CONFIG_MES);
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"帮助", "#帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void configListen1(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, helpMsg);
    }

    //---------------------
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"其他功能帮助", "#其他功能帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void otherhelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, OTHER_HELP_MSG);
    }


    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"会战帮助", "#会战帮助", "2"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
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
    @Filter(value = {"工会帮助", "#工会帮助", "公会帮助", "#公会帮助", "3"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void grouphelpListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, GROUP_HELP_MSG);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"工会帮助", "#工会帮助", "#公会帮助", "公会帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void grouphelpListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, GROUP_HELP_MSG);
    }

    @Listen(value = MsgGetTypes.groupMsg)
    @Filter(value = {"bilibili相关帮助", "#bilibili相关帮助"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void bilibiliListen(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, BilibiliMsg);
    }

    @Listen(value = MsgGetTypes.privateMsg)
    @Filter(value = {"bilibili相关帮助", "#bilibili相关帮助", "5"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void bilibiliListen(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg, BilibiliMsg);
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
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), CQCodeUtil.build().getCQCode_At(msg.getQQCode())+ gashapon.getData());
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

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"重置扭蛋cd"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void clearniudan(GroupMsg msg, MsgSender sender) {
        if (pricnessConfig.getMasterQQ().equals(msg.getQQCode())) {
            coolDown.clear();
        }
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "已清除cd信息");
    }


    /**
     * 普通池的概率
     *
     * @param num
     * @return
     */
    @Ignore
    private Gashapon dp_Gashapon(int num) {
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
    private Gashapon dp_UpGashapon(int num) {
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
            if(num>10){
                g.setBan(num / thre < 20);
            }else {
                if(thre>1){
                    g.setBan(true);
                }else {
                    g.setBan(false);
                }
            }
        } catch (ArithmeticException e) {
            g.setBan(false);
        }
        return g;
    }

    /**
     * 组织抽卡结果
     */
    @Ignore
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
        if(list.size()>0){
            try {
                File file = composeImg(list);
                if (file.exists()) {
                    stringBuilder.append(CQCodeUtil.build().getCQCode_Image(file.getAbsolutePath()).toString());
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
    public void kimo(GroupMsg msg, MsgSender sender) throws IOException {
        Random random = new Random();
        random.setSeed(new Date().getTime());
        String send=null;
        File[] files = HeitaiFile.listFiles();

        if(files!=null){
            int i = kimo_Definde.length + files.length;
            int j = random.nextInt(i);
            if (j > kimo_Definde.length - 1) {
                CQCodeUtil build = CQCodeUtil.build();
                CQCode cqCode_image = build.getCQCode_Image(files[j - kimo_Definde.length].getAbsolutePath());
                send = cqCode_image.toString();
            } else {
                send = kimo_Definde[random.nextInt(kimo_Definde.length)];
            }
        }else {
            HeitaiFile.mkdirs();
            File file=new File(HeitaiFile,"机器人扫描这个文件夹下的图片文件用以作为老婆指令的应答,这个是示例图片文件.jpg");
            file.createNewFile();
            InputStream inputStream = ImageUtil.class.getResourceAsStream("/image/laopo.jpg");
            byte[] b=new byte[1024];
            while(inputStream.read(b)>0){
                FileUtils.writeByteArrayToFile(file,b,true);
            }
            send = kimo_Definde[random.nextInt(kimo_Definde.length)];

        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), send);
    }

    @Listen(value = {MsgGetTypes.groupMsg, MsgGetTypes.privateMsg})
    @Filter(value = "切噜.*")
    public void qielu(MsgGet msg, MsgSender sender) {
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
            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg).getGroupCode(), tranled.toString());
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg).getQQCode(), tranled.toString());
            }

        } else {
            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg).getGroupCode(), "没有要翻译的语句哎");
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg).getQQCode(), "没有要翻译的语句哎");
            }
        }
    }

    @Listen(value = {MsgGetTypes.groupMsg, MsgGetTypes.privateMsg})
    @Filter(value = "翻译切噜.*")
    public void reqielu(MsgGet msg, MsgSender sender) {
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

            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg), String.valueOf(getChars(bytes.toArray(new Byte[0]))));
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg), String.valueOf(getChars(bytes.toArray(new Byte[0]))));
            }

        } else {
            if (msg instanceof GroupMsg) {
                sender.SENDER.sendGroupMsg(((GroupMsg) msg), "没有要翻译的语句哎");
            } else {
                sender.SENDER.sendPrivateMsg(((PrivateMsg) msg), "没有要翻译的语句哎");
            }
        }

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
    private void reFlashCoolDown(String QQ) {
        LocalDateTime localDateTime = LocalDateTime.now();

        if (coolDown == null) {
            coolDown = new HashMap<>();
            coolDown.put(QQ, localDateTime.plusSeconds(pricnessConfig.getGashaponcool()));
        } else {
            coolDown.put(QQ, localDateTime.plusSeconds(pricnessConfig.getGashaponcool()));
        }
    }

    /**
     * 获取冷却时间是不是到了
     *
     * @param QQ
     * @return
     */
    private boolean isCool(String QQ) {
        if (coolDown == null) {
            coolDown = new HashMap<>();
            return true;
        } else {
            if (coolDown.get(QQ) != null) {
                return coolDown.get(QQ).isBefore(LocalDateTime.now());
            } else {
                return true;
            }
        }
    }


}
