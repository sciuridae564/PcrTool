package cn.sciuridae;

import java.util.HashMap;

public class constant {
    public static final String helpMsg = "常规指令介绍：\n" +
            "1.创建工会，[ #建会 @机器人 工会名 游戏昵称]\n" +
            "2.加入工会，[#入会 @机器人 游戏昵称]\n" +
            "3.批量加入工会，[#批量入会 @入会成员1 @入会成员2 ...]\n" +
            "（注意：使用这个命令需为工会长）\n" +
            "4.退出工会，[#退会 @机器人]\n" +
            "5.开始进行会战，[#开始会战]\n" +
            "（注意：需为工会长）\n" +
            "5.5开始会战，但不是今天[#开始会战 年：月：日]\n" +
            "例，若今天为20年5月1号，5月3号时开始会战则输入\n[#开始会战 20:05:03]  注意为英文状态\n" +
            "6.查询当前boss信息[boss状态]\n" +
            "7.查询今日未出刀情况，[#未出刀]\n" +
            "8.出刀，[#出刀]\n" +
            "9.挂树，[#挂树]\n" +
            "10.收刀，[#收刀 伤害值]\n" +
            "11.撤回成员的出刀记录，[撤刀 出刀编号]\n" +
            "12.结束会战，[#结束会战]\n" +
            "（注意：需为工会长)\n" +
            "13.抽十发白金池子，[十连]\n" +
            "14.抽十发松鼠up池子，[up十连]\n" +
            "15.喊老婆，[老婆 @机器人]\n" +
            "16.抽300发白金池子，[井]\n" +
            "17.抽300发松鼠池子，[up井]\n" +
            "--\n" +
            "公会指令：\n" +
            "1.出刀编号可用查已出刀命令查看\n" +
            "\t查看当日已出刀信息[已出刀 （@要查的那个人）]\n" +
            "2.后面不加@则默认查当日全部已出刀情况\n" +
            "\t代替成员出刀[代刀 @那个人 伤害值]\n" +
            "3.和交刀命令一样，可以不用出刀直接交刀\n" +
            "\t调整boss的周目，血量，第几个[调整boss状态 （调整后的）周目 几王 血量]\n" +
            "\t（例：调整boss状态 2 4 2554）\n" +
            "\t结果：将boss调整到二周目 4王位 2554血量剩余\n" +
            "4.查看已加入工会 的成员列表，[工会成员列表]\n" +
            "5.查看工会一些基本信息，[工会信息]\n" +
            "6.更改工会名字，[改工会名 更改后的工会名]\n" +
            "7.更改自己的名字，[改名 更改后的名字]\n" +
            "8.设置一个人为管理员 [设置管理 @那个人]" +
            "9.将一个人踢出工会[踢人 @那个人]" +
            "";


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
    public static final String notBossOrNotDate = " 工会战还没开启呢";
    public static final String noFindTheOne = "没有找到这个人，是不是还没有入会？";
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
