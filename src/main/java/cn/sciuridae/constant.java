package cn.sciuridae;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class constant {
    public static final String helpMsg = "命令总览：\n" +
            "打*为管理员指令**的为工会长指令\n" +
            "工会基础指令：\n" +
            "\t1.创建工会[#建会 @机器人 工会名 游戏昵称]\n" +
            "\t2.加入工会[#入会 @机器人 游戏昵称]\n" +
            "\t3.*批量加入工会[#批量入会 @入会成员1 @入会成员2 ...]\n" +
            "\t4.退出工会，[#退会 @机器人]\n" +
            "\t\t注意：会长退会则为解散工会\n" +
            "\t5.*将一个人踢出工会[踢人 @那个人]\n" +
            "\t6.*更改工会名字[改工会名 更改后的工会名]\n" +
            "    7.更改自己的名字[改名 更改后的名字]\n" +
            "\t8.**转让会长权限（保留自己的管理员权限)[转让会长 @那个人]\n" +
            "\t9.**设置一个人为管理员[设置管理 @那个人]\n" +
            "\t10.**撤销他人的管理权限[撤下管理 @那个人]\n" +
            "\t11.查看已加入工会 的成员列表[工会成员列表]\n" +
            "    12.查看工会一些基本信息[工会信息]\n" +
            "工会战命令:\n" +
            "\t1.开始进行会战[#开始会战 (时间)]\n" +
            "\t\t时间参数可选，默认为今天\n" +
            "\t\t时间格式举例：20：05：06\n" +
            "\t2.结束会战[#结束会战]\n" +
            "\t3.表示自己正在出刀[#出刀]\n" +
            "\t4.表示自己因为太菜挂树了[#挂树]\n" +
            "\t5.出完刀向机器人提交成绩[#收刀 伤害值]\n" +
            "\t\t如果有自信不会挂树可以直接用这个命令提交伤害\n" +
            "\t\t但是如果挂了请注意不要透露给工会长自己的住宅地址，注意人身安全\n" +
            "\t\t机器人不会接受没有提交出刀请求后的挂树命令\n" +
            "\t6.*代替别人提交伤害数据[代刀 @那个人 伤害值]\n" +
            "\t7.*撤除出刀资料[撤刀 出刀编号]\n" +
            "\t\t这个命令只是删除了出刀信息，但是并不会更改boss信息，防止发生奇奇怪怪的事情\n" +
            "\t\t需要管理手动调整\n" +
            "\t8.查询现在的boss信息[boss状态]\n" +
            "\t9.查看正在出刀（非挂树）的人[正在出刀]\n" +
            "\t10.查看正在树上的人[查树]\n" +
            "\t11.查询今日全部成员未出刀情况，[#未出刀 (@那个人)]\n" +
            "\t\t同下\n" +
            "\t12.查询今日全部成员已出刀情况，[#已出刀 (@那个人)]\n" +
            "\t\t没有@其他人的情况下默认查看整个工会\n" +
            "\t13.*调整boss血量，周目等一系列信息[调整boss状态 （调整后的）周目 几王 血量]\\\n" +
            "\t\t例：调整boss状态 2 4 2554 为调整到二周目4王2554剩余血量\n" +
            "杂项指令：\n" +
            "\t1.抽一发井(300抽)[(up)井]\n" +
            "\t\t带up就是up池，不带up就是白金池\n" +
            "\t2.抽一发十连(10抽)[(up)十连]\n" +
            "\t3.加 密 通 话[切噜 要加密的话]\n" +
            "\t3.有 内 鬼，终 止 交 易[翻译切噜 加密的话]\n" +
            "\t4.生成excel统计表格[生成excel (时间)]\n" +
            "\t\t时间参数与上面相同\n" +
            "默认设定：\n" +
            "\t每天5点检查树上的人，全 部 撸 下\n" +
            "\t每天0点检查工会战结束的工会，生成一个总表\n" +
            "\t生成的excel默认在jar文件旁边的excel文件夹下\n" +
            "\texcel文件格式为 群号%时间\n" +
            "\t俺最可爱\n" +
            "\t每次更新都会带来一些新bug，这是世界的真理\n" +
            "\t抽一发井是有冷却的，大概2分钟吧\n" +
            "\t看boss状态不止可以使用上面那个指令，还可以试试boss, boss咋样了, boss还好吗\n" +
            "\t设置管理员同理 设置管理, 设置管理员, 添加管理员, 添加管理\n" +
            "\t输入老婆并@机器人会被机器人顺着网线过来真人格斗\n" +
            "\t只要是管理员就可以踢掉任何人（不包括工会长）";


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
    public static final String successDropGroup = "成功销毁工会，一切都已不复存在";
    public static final String successChangeSuperPower = "更换会长成功，为王的诞生献上礼炮";
    public static final String noGroupOutGroup = "没有工会退什么工会辣";
    public static final String isTree="已经挂牢了，不要想偷偷从树上溜走了哟♥";
    public static final String noInDataBase="¿,他群间谍发现，建议rbq一周";
    public static final String notInTree = "啊这，俺寻思你也妹出刀啊";
    public static final String commandError="啊咧咧，这个命令格式我看不懂鸭";
    public static final String noThisGroup = "啊咧咧，本群的工会好像还没有建立呢";
    public static final String NoGroupFightDate = " 未开启工会战或还未加入工会";
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
    public static final String ExcelDir = "Excel/";
    public static final SimpleDateFormat df = new SimpleDateFormat(dateFormat);
    public static final String fileTimeFormat = "yy年MM月dd日";
    public static final SimpleDateFormat dfForFile = new SimpleDateFormat(fileTimeFormat);


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
