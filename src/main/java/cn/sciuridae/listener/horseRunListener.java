package cn.sciuridae.listener;

import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.service.ScoresService;
import cn.sciuridae.utils.bean.Horse;
import cn.sciuridae.utils.bean.groupPower;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.messages.types.PowerType;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.listener.prcnessIntercept.On;

@Service
public class horseRunListener {

    public static final int signScore = 5000;
    private static HashMap<String, Map<Long, int[]>> maList = new HashMap();//赛马  群号->映射群员->映射押注对象号码 押注金额
    @Autowired
    ScoresService ScoresServiceImpl;
    /**
     * bot管理器
     */
    @Autowired
    private BotManager botManager;

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#赛马.*", at = true)
    public void startHorse(GroupMsg msg, MsgSender sender) {
        //必须开启才可以开始比赛
        if (On.get(msg.getGroupCode()).isHorse()) {
            //比赛只能同时开启一次
            if (maList.get(msg.getGroupCode()) != null) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已经有比赛在进行了");
            } else {
                maList.put(msg.getGroupCode(), new HashMap<Long, int[]>());
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "赛马比赛开盘，有钱交钱妹钱交人");
            }
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#给xcw上供", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void sign(GroupMsg msg, MsgSender sender) {
        if (On.get(msg.getGroupCode()).isHorse()) {
            Scores byId = ScoresServiceImpl.getById(msg.getQQCodeNumber());
            if (byId != null) {
                if (byId.getiSign()) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "xcw认为奇怪可疑的变态份子只能一天上供一次");
                    return;
                }
                byId.setScore(byId.getScore() + signScore);
                byId.setiSign(true);
                ScoresServiceImpl.updateById(byId);
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "xcw对奇怪可疑的变态份子表示感谢，马币+5000，现在马币:" + byId.getScore());
            } else {
                byId = new Scores();
                byId.setQQ(msg.getQQCodeNumber());
                byId.setiSign(true);
                byId.setScore(signScore);
                ScoresServiceImpl.save(byId);
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "xcw对奇怪可疑的变态份子表示感谢，马币+5000");
            }
        }
    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#开始赛马.*", at = true)
    public void start(GroupMsg msg, MsgSender sender) {
        if (maList.get(msg.getGroupCode()) != null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "赛马开始辣，走过路过不要错过");
            Horse horse = new Horse();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), drawHorse(horse));
            horseFight horseFight = new horseFight(msg.getGroupCode(), horse);
            horseFight.start();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "押马[1-5]#[0-9]*")
    public void buyHorse(GroupMsg msg, MsgSender sender) {
        String re = "^押马([1-5])#([0-9]{1,})$";
        String str = msg.getMsg();
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(str);
        int no = 0, coin = 0;
        while (m.find()) {
            no = Integer.parseInt(m.group(1));
            coin = Integer.parseInt(m.group(2));
        }
        if (no > 5 || no < 1) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有这个编号的选手");
            return;
        }
        if (coin < 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "反向下注不可取");
            return;
        }
        Scores byId = ScoresServiceImpl.getById(msg.getQQCodeNumber());
        if (byId == null || byId.getScore() - coin < 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没那么多可以下注的币惹");
            return;
        }

        if (maList.get(msg.getGroupCode()).get(msg.getQQCodeNumber()) != null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "你已经下过注辣");
        } else {
            int[] integers = new int[2];
            integers[0] = no - 1;
            integers[1] = coin;
            maList.get(msg.getGroupCode()).put(msg.getQQCodeNumber(), integers);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "下注完成 加油啊" + no + "号马");
        }

    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "我有多少钱鸭老婆", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void mycoin(GroupMsg msg, MsgSender sender) {
        Scores byId = ScoresServiceImpl.getById(msg.getQQCodeNumber());
        if (byId != null) {
            if (byId.getiSign()) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "[CQ:at,qq=" + msg.getQQCode() + "] 有" + byId.getScore() + "块钱");
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "[CQ:at,qq=" + msg.getQQCode() + "] 有" + byId.getScore() + "块钱，还没有签到哦");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "[CQ:at,qq=" + msg.getQQCode() + "] 锅里没有一滴油");
        }
    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启赛马"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void openhorse(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQ()).getPowerType();
            if (powerType.isOwner() || powerType.isAdmin()) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setHorse(true));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启赛马");
                setjson();
            }
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启赛马");
            setjson();
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭赛马"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shuthorse(GroupMsg msg, MsgSender sender) {
        try {
            PowerType powerType = sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQ()).getPowerType();
            if (powerType.isOwner() || powerType.isAdmin()) {
                On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setHorse(false));
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭赛马");
                setjson();
            }
        } catch (NullPointerException e) {
            groupPower groupPower = new groupPower();
            groupPower.setHorse(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭赛马");
            setjson();
        }
    }


    /**
     * 计算这个干扰能不能奏效
     *
     * @return
     */
    public boolean isCan() {
        Random random = new Random();
        return random.nextInt(33) > 22;
    }

    /**
     * 根据传入的马赛场实况类，制作出马赛场图
     *
     * @param horse
     * @return
     */
    public String drawHorse(@NotNull Horse horse) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < horse.getPosition().size(); i++) {
            stringBuilder.append(i + 1);
            for (int j = 0; j < 9 - horse.getPosition().get(i); j++) {
                stringBuilder.append("Ξ"); //
            }
            stringBuilder.append(emojis[horse.getType().get(i)]);//画马
            for (int j = 0; j < horse.getPosition().get(i) - 1; j++) {
                stringBuilder.append("Ξ");//
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void allClear(String groupQQ, int winer) {
        Map<Long, int[]> group = maList.get(groupQQ);
        Iterator<Long> iterator = group.keySet().iterator();
        List<Scores> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Long entry = iterator.next();
            if (group.get(entry)[0] == winer) {
                Scores byId = ScoresServiceImpl.getById(entry);
                byId.setScore((int) (byId.getScore() + group.get(entry)[1] * 1.5));
                list.add(byId);
            } else {
                Scores byId = ScoresServiceImpl.getById(entry);
                byId.setScore(byId.getScore() - group.get(entry)[1]);
                list.add(byId);
            }
        }
        ScoresServiceImpl.updateBatchById(list);
        maList.remove(groupQQ);
    }

    public class horseFight extends Thread {
        private String groupQQ;
        private Horse horse;
        private List<Integer> horselist;
        private boolean fightOver = true;
        private int winer;

        public horseFight(String groupQQ, Horse horse) {
            this.groupQQ = groupQQ;
            this.horse = horse;
            horselist = horse.getPosition();
        }

        @Override
        public void run() {
            final BotSender sender = botManager.defaultBot().getSender();
            Random random = new Random();
            while (fightOver) {
                try {
                    Thread.sleep(random.nextInt(1000) + 2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(groupQQ);
                String s = event();
                System.out.println(s);
                sender.SENDER.sendGroupMsg(groupQQ, s);//事件发生器
                add();//所有马向前跑一格
                sender.SENDER.sendGroupMsg(groupQQ, drawHorse(horse));
                try {
                    Thread.sleep(random.nextInt(1000) + 3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            allClear(groupQQ, winer);//收钱
            sender.SENDER.sendGroupMsg(groupQQ, drawHorse(horse));//最后再画一次马图
            sender.SENDER.sendGroupMsg(groupQQ, winer + 1 + "最终赢得了胜利，让我们为他鼓掌");
        }

        public void add() {
            List<Integer> list = horse.getPosition();
            for (int i = 0; i < horse.getPosition().size(); i++) {
                int j = list.get(i) + 1;
                list.set(i, j);
                //一个马跑完全程停止
                if (j > 9) {
                    fightOver = false;
                    winer = i;//记录下冠军
                }
            }
        }

        public String event() {
            Random random = new Random();
            //计算这次发生的是好事还是坏事
            if (random.nextInt(77) > 32) {
                //好事
                int i = random.nextInt(horse.getPosition().size());//作用于哪只马
                horselist.set(i, horselist.get(i) + 1);
                return horeEvent.getGoodHorseEvent().get(random.nextInt(horeEvent.getGoodHorseEvent().size())).replace("?", String.valueOf(i));
            } else {
                //坏事
                int i = random.nextInt(horse.getPosition().size());
                horselist.set(i, horselist.get(i) - 1);
                return horeEvent.getBedHorseEvent().get(random.nextInt(horeEvent.getBedHorseEvent().size())).replace("?", String.valueOf(i));
            }
        }


    }

}
