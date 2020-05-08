package cn.sciuridae;

import java.util.HashMap;

public class constant {
    public static final String helpMsg = "指令介绍：\n" +
            "创建工会，[ #建会 @机器人 工会名 游戏昵称]\n" +
            "例：#建会 @机器人 有一个工会 切噜\n" +
            "加入工会，[#入会 @机器人 游戏昵称]\n" +
            "例：#入会 @机器人 切噜\n" +
            "批量加入工会，[#批量入会 @机器人 @入会成员1 @入会成员2 ...]\n" +
            "例：#入会 @机器人 切噜 切噜1 切噜2\n" +
            "注意：使用这个命令需为工会长\n" +
            "退出工会，[#退会 @机器人]\n" +
            "例：#退会\n" +
            "查询今日出刀情况[#查刀]\n" +
            "例：太简单没有例子\n" +
            "出刀[#出刀]\n" +
            "例：太简单没有例子\n" +
            "上树[#上树]\n" +
            "例：太简单没有例子\n" +
            "收刀[#收刀]\n" +
            "例：太简单没有例子\n" +
            "开始进行会战，[#开始会战]\n" +
            "注意：需为工会长\n" +
            "结束会战，[#结束会战]\n" +
            "注意：需为工会长\n" +
            "将一句话翻译成切噜语（不完善）[切噜 要翻译的那句话]\n" +
            "例 切噜 列克星敦是世界的主宰\n" +
            "机器人回复：啪噼切泼啦哩...\n" +
            "上个命令的反向操作[翻译切噜 要翻译的那句话]\n" +
            "例 翻译切噜 啪噼切泼啦哩...\n" +
            "机器人回复：列克星敦是世界的主宰\n" +
            "抽十发白金池子[十连]\n" +
            "抽十发松鼠up池子[up十连]\n" +
            "喊老婆[老婆 @机器人]\n" +
            "抽300发白金池子[井]\n" +
            "抽300发松鼠池子[up井]\n";


    public static final String coolQAt="[CQ:at,";
    public static final String createGroupMuchAt="建团指令中的@人数太多力，究竟哪个是团长呢";
    public static String robotQQ= "0";//机器人qq
    public static final String dateFormat="yy:MM:dd";
    public static final int knifeFrash=4;//工会战次数刷新在4点
    public static final String clearTree="工会刀次数已刷新，强制下树惹 ";//强制下树提示
    public static final String error="好像发生了点小错误";
    public static final String tips_error="指令语法有点不对呢，人家读不懂~";
    public static final String isHaveGroup="已经有工会了还想加别的工会，花心大萝卜";
    public static final String isFullGroup="这个工会已经满员啦，再和会长多py一次吧";
    public static final String successJoinGroup="成功加入工会啦，记得每天好好女装哦";
    public static final String successOutGroup="成功退出工会啦，记得每天好好男装哦";
    public static final String noGroupOutGroup = "没有工会退什么工会辣";
    public static final String isTree="已经挂牢了，不要想偷偷从树上溜走了哟♥";
    public static final String noInDataBase="¿,他群间谍发现，建议rbq一周";
    public static final String notInTree="啊这，俺寻思树上也妹你影啊";
    public static final String commandError="啊咧咧，这个命令格式我看不懂鸭";
    public static final String noThisGroup="啊咧咧，这个工会好像还没有建立呢";
    public static final String allVoidKnife = " 全员懒狗，一个刀都没出";
    public static final String notBossOrNotDate = " 还没开启工会战";
    public static final int[] BossHpLimit = {6000000, 8000000, 10000000, 12000000, 20000000};//各个boss的血量上限
    public static final String notPower = "得工会管理员才可以惹";
    public static final String SuccessStartFight = "会战开始惹";
    public static final String StartFightStartDouble = "会战已经开始惹，为什么还要再开一次";
    public static final String SuccessEndFight = "会战结束惹";
    public static final String EngFightStartDouble = "没有正在进行的会战惹";
    public static final String[] one = {"日和莉", "怜", "未奏希", "胡桃", "依里", "由加莉", "铃莓", "碧", "美咲", "莉玛"};
    public static final String[] two = {"茜里", "宫子", "雪", "铃奈", "香织", "美美", "惠理子", "忍", "真阳", "栞", "千歌", "空花", "珠希", "美冬", "深月", "铃"};
    public static final String[] Three = {"杏奈", "真步", "璃乃", "初音", "依绪", "咲恋", "望", "妮胧", "秋乃", "真琴", "静流", "莫妮卡", "姬塔", "纯"};
    public static final double Three_chance = 0.0015;
    public static final double two_chance = 0.0075;
    public static final double one_chance = 0.0772;
    public static final double Three_chance_plus = 0.0075;
    public static final double two_chance_plus = 0.040416;
    public static final double Three_chance_ten = 0.0035;
    public static final double two_chance_ten = 0.040416;
    public static final String[] Three_plus = {"真步", "真琴"};
    public static final String[] two_plus = {"真阳", "栞", "香织", "铃"};
    public static final String[] one_plus = {"莉玛"};
    public static HashMap<String, String> pcrGroupMap;//启用prc群里的qq号：昵称映射表
    public static final String[] kimo_Definde = {"hentai，谁是你老婆啦", "死肥宅一边玩去啦，不要打扰我", "本小姐不想理你，并向你扔了一只胖次",
            "无应答......", "嗷呜%_%", "谁是你老婆啦，哼"};
    public static final String[] QieLU = {"切噜", "切哩", "切吉", "噜拉", "啪噜", "切璐", "扣", "啦哩", "啦嘟", "切泼", "啪噼", ",", "嚕嚕", "啰哩", "切拉", "切噼"};
    public static HashMap<String, Integer> reQieLU = new HashMap<>();

    static {
        reQieLU.put("切噜", 0);
        reQieLU.put("切哩", 1);
        reQieLU.put("切吉", 2);
        reQieLU.put("噜拉", 3);
        reQieLU.put("啪噜", 4);
        reQieLU.put("切璐", 5);
        reQieLU.put("扣扣", 6);
        reQieLU.put("啦哩", 7);
        reQieLU.put("啦嘟", 8);
        reQieLU.put("切泼", 9);
        reQieLU.put("啪噼", 10);
        reQieLU.put("%%", 11);
        reQieLU.put("嚕嚕", 12);
        reQieLU.put("啰哩", 13);
        reQieLU.put("切拉", 14);
        reQieLU.put("切噼", 15);
    }
}
