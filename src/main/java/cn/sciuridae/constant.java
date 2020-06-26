package cn.sciuridae;

import cn.sciuridae.utils.bean.HoreEvent;
import cn.sciuridae.utils.bean.PricnessConfig;
import cn.sciuridae.utils.bean.groupPower;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static cn.sciuridae.listener.prcnessIntercept.On;


public class constant {
    public static final String helpMsg = "1.其他功能帮助\n2.会战帮助\n3.工会帮助\n4.机器人设置\n5.bilibili相关帮助\n请选择命令提示菜单";

    public static final String BilibiliMsg = "以下全部为私聊\n1.获取视频封面图片 [视频封面 av/bv号]\nav与bv需统一大小写\n例: 视频封面 av1145124 \n" +
            "2.av/bv转换 \n直接输入av/bv号即可 \n例: av11458\n" +
            "3.当前up直播状态 [直播 upuid]\n仅仅是b站直播，其他站的在计划中.jpg\n" +
            "4.设置开播提示 [设置开播提示 up的uid]\n" +
            "5.查看开播提示槽位使用情况 [查看开播提示]\n" +
            "6.清除一个开播槽位上的记录 [清除开播提示 槽位]\n示例 清除开播提示 1\n" +
            "直播信息均一分钟刷新一次";

    public static final String CONFIG_MES = "设置文件均可在机器人运行后自动生成，有些配置项没有自动生成删掉对应的配置文件再[重载设置]即可\n修改配置文件必须使用重载设置命令才可使用编辑后的配置" +
            "1.各个群内的开关，保存在config.txt中，true为开启，false为关闭\n若要修改则找到对应群号修改对应值即可\n2.扭蛋池信息保存在扭蛋.txt中 概率以千分之整数存储\n例如up三星出率为千分之七，则设置为7\n若要更换抽卡所发送的人物图片资源，则在旁边建以 image 文件夹\n向里面放入人物资源图片，文件以扭蛋.,txt中设置的人物名字为准，格式为png" +
            "3.赛马事件则为 事件.txt文件 中\n赛马所受影响的马号以?占位\n例如有一个事件为 3号马摔倒了 它是坏事件，将其放入bedHorseEvent后面中括号里面，写入3号马摔倒了";

    public static final String GROUP_HELP_MSG = "命令总览：\n" +
            "打*为管理员指令**的为工会长指令 中括号内为指令不需要中括号 小括号内为可选内容 ，-为需要主人qq\n" +
            "工会基础指令：\n" +
            "\t1.创建工会[#建会 @机器人 工会名 游戏昵称]\n" +
            "\t2.加入工会[#入会 @机器人 游戏昵称]\n" +
            "\t3.*批量加入工会[#批量入会 @入会成员1 @入会成员2 ...]\n" +
            "\t4.退出工会，[#退会 @机器人]\n" +
            "\t\t注意：会长退会则为解散工会\n" +
            "\t5.*将一个人踢出工会[#踢人 @那个人(那个人的qq号)]\n" +
            "\t6.*更改工会名字[#改工会名 更改后的工会名]\n" +
            "    7.更改自己/别人的名字[#改名 (@那个人) 更改后的名字]\n" +
            "\t8.**转让会长权限（保留自己的管理员权限)[转让会长 @那个人]\n" +
            "\t9.**设置一个人为管理员[设置管理 @那个人]\n" +
            "\t10.**撤销他人的管理权限[撤下管理 @那个人]\n" +
            "\t11.查看已加入工会 的成员列表[工会成员列表]\n" +
            "    12.查看工会一些基本信息[工会信息]\n";

    public static final String FIGHT_HELP_MSG = "工会战命令:\n" +
            "\t1.开始进行会战[#开始会战 (时间)]\n" +
            "\t\t时间参数可选，默认为今天\n" +
            "\t\t时间格式举例：2020:05:06\n" +
            "\t默认结束时间为8天后，即会战时间为\n" +
            "\t2020-06-05 5点到2020-06-13 0点\n" +
            "\t2.结束会战[#结束会战]\n" +
            "\t3.表示自己正在出刀[#出刀]\n" +
            "\t4.表示自己因为太菜挂树了[#挂树]\n" +
            "\t5.出完刀向机器人提交成绩[#收刀 伤害值]\n" +
            "\t\t如果有自信不会挂树可以直接用这个命令提交伤害\n" +
            "\t\t但是如果挂了请注意不要透露给工会长自己的住宅地址，注意人身安全\n" +
            "\t\t机器人不会接受没有提交出刀请求后的挂树命令\n" +
            "\t6.*代替别人提交伤害数据[#代刀 @那个人 伤害值]\n" +
            "\t7.*撤除出刀资料[#撤刀 出刀编号]\n" +
            "\t\t这个命令只是删除了出刀信息，但是并不会更改boss信息，防止发生奇奇怪怪的事情\n" +
            "\t\t需要管理手动调整\n" +
            "\t8.查询现在的boss信息[#调整boss状态]\n" +
            "\t9.查看正在出刀（非挂树）的人[#正在出刀]\n" +
            "\t10.查看正在树上的人[#查树]\n" +
            "\t11.查询今日全部成员未出刀情况，[未出刀 (@那个人)]\n" +
            "\t\t同下\n" +
            "\t12.查询今日全部成员已出刀情况，[已出刀 (@那个人)]\n" +
            "\t\t没有@其他人的情况下默认查看整个工会\n" +
            "\t13.*调整boss血量，周目等一系列信息[调整boss状态 （调整后的）周目 几王 血量]\\\n" +
            "\t\t例：调整boss状态 2 4 2554 为调整到二周目4王2554剩余血量\n";

    public static final String OTHER_HELP_MSG = "杂项指令：\n" +
            "\t1.抽一发井(300抽)[#(up)井]\n" +
            "\t\t带up就是up池，不带up就是白金池\n" +
            "\t2.抽一发十连(10抽)[#(up)十连]\n" +
            "\t2.抽n发[#(up)抽卡 n]\n" +
            "\t-2.清除所有人的抽卡cd,私聊机器人 [清除扭蛋cd]\n" +
            "\t3.加 密 通 话[切噜 要加密的话]\n" +
            "\t3.有 内 鬼，终 止 交 易[翻译切噜 加密的话]\n" +
            "\t4.生成excel统计表格[#生成excel (时间)]\n" +
            "\t\t时间参数与上面相同\n" +
            "\t5.获取登陆码[获取码],如果没码就会强行造一个码\n" +
            "\t5.5登陆码更换 私聊机器人 [更换token]\n" +
            "\t6.在一个群里关闭/开启这个机器人[#关闭/开启PcrTool]\n" +
            "\t注意.这个关闭为总按钮，需群管才能使用\n" +
            "\t7.在一个群里关闭/开启扭蛋[#关闭/开启扭蛋]\n" +
            "\t8.在一个群里关闭/开启提醒买药小助手[#关闭/开启提醒买药小助手]\n" +
            "\t9.读取使用机器人通用设置文件和扭蛋信息，私聊[重载设置]\n" +
            "\t10.查看现在机器人设置,私聊[通用设置]" +
            "\t11.查看群内开关设置[#查看本群设置]\n" +
            "\t11.赌马准备[#赛马 @机器人]\n" +
            "\t12.下注  [押马马号#金额] 示例 押马1#125\n" +
            "\t12.赌马开始[#开始赛马 @机器人]\n" +
            "\t13.关闭赌马[#关闭赛马]\n" +
            "\t14.开启赌马[#开启赛马]\n" +
            "\t15.每天免费币 [#给xcw上供]\n" +
            "\t查看自己有多少币 [我有多少钱鸭老婆]\n" +
            "\t查看群内设置 [#查看本群设置]\n" +
            "\t看漂亮小姐姐 私聊机器人[我要看漂亮小姐姐]\n" +
            "\t使所有人的签到状态为未签[刷新全部签到]\n" +
            "\t使机器人在对老婆做出反应时发送自定义图片[挂载变态图片]\n" +
            "默认设定：\n" +
            "\t这个插件和插件的所有功能都是开启的\n" +
            "\t每天5点检查树上的人，全 部 撸 下\n" +
            "\t每天0点检查工会战结束的工会，生成一个总表\n" +
            "\t生成的excel默认在jar文件旁边的excel文件夹下\n" +
            "\texcel文件格式为 群号%时间\n" +
            "\t抽一发井是有冷却的，具体问开这个机器人的人\n" +
            "\t看boss状态不止可以使用上面那个指令，还可以试试boss, boss咋样了, boss还好吗\n" +
            "\t设置管理员同理 设置管理, 设置管理员, 添加管理员, 添加管理\n" +
            "\t输入老婆并@机器人会被机器人顺着网线过来真人格斗\n" +
            "\t只要是管理员就可以踢掉任何人（不包括工会长）" +
            "\t自定义卡池部分：\n" +
            "\t卡池图片会优先从jar同级文件夹下的image文件夹下找\n" +
            "\t图片名称为扭蛋.txt文件中的人物名，后缀为png\n" +
            "\t例：扭蛋.txt中的白金池中有 镜华 则image下有一个镜华.png的图片\n";


    public static final String coolQAt = "[CQ:at,qq=";
    public static final String dateFormat = "yy:MM:dd";
    public static final int knifeFrash = 4;//工会战次数刷新在4点
    public static final String clearTree = "工会刀次数已刷新，强制下树惹 ";//强制下树提示
    public static final String error = "好像发生了点小错误";
    public static final String isHaveGroup = "已经有工会了还想加别的工会，花心大萝卜";
    public static final String isFullGroup = "这个工会已经满员啦，再和会长多py一次吧";
    public static final String successJoinGroup = "成功加入工会啦，记得每天好好女装哦";
    public static final String successOutGroup = "成功退出工会啦，记得每天好好男装哦";
    public static final String successDropGroup = "成功销毁工会，一切都已不复存在";
    public static final String successChangeSuperPower = "更换会长成功，为王的诞生献上礼炮";
    public static final String noGroupOutGroup = "没有工会退什么工会辣";
    public static final String isTree = "已经挂牢了，不要想偷偷从树上溜走了哟♥";
    public static final String commandError = "啊咧咧，这个命令格式我看不懂鸭";
    public static final String noThisGroup = "啊咧咧，本群的工会好像还没有建立呢";
    public static final String NoGroupFightDate = " 未开启工会战或还未加入工会";
    public static final String notBossOrNotDate = " 工会战还没开启呢";
    public static final String noFindTheOne = "没有找到这个人，是不是还没有入会？";
    public static final int[] BossHpLimit = {6000000, 8000000, 10000000, 12000000, 20000000};//各个boss的血量上限
    public static final String notPower = "权限不足，或未建立工会";
    public static final String SuccessStartFight = "会战开始惹";
    public static final String StartFightStartDouble = "会战已经开始惹，为什么还要再开一次";
    public static final String SuccessEndFight = "会战结束惹";
    public static final String EngFightStartDouble = "没有正在进行的会战惹";
    public static List<String> kimo_Definde_image = null;
    public static final String[] kimo_Definde = {"hentai，谁是你老婆啦", "死肥宅一边玩去啦，不要打扰我", "本小姐不想理你，并向你扔了一只胖次",
            "无应答......", "嗷呜%_%", "谁是你老婆啦，哼", "对方无应答", "你是个好人", "对不起!您拨打的用户暂时无法接通,请稍后再拨.Sorry!The subscriber you dialed can not be connected for the moment, please redial later."
            , "傲娇与偏见", "刑法第二百三十六条 强奸罪\n" +
            "以暴力、胁迫或者其他手段强奸妇女的，处三年以上十年以下有期徒刑。 奸淫不满十四周岁的幼女的，以强奸论，从重处罚。 强奸妇女、奸淫幼女，有下列情形之一的，处十年以上有期徒刑、无期徒刑或者死刑： （一）强奸妇女、奸淫幼女情节恶劣的； （二）强奸妇女、奸淫幼女多人的； （三）在公共场所当众强奸妇女的； （四）二人以上轮奸的； （五）致使被害人重伤、死亡或者造成其他严重后果的。",
            "第二百三十七条 强制猥亵、侮辱罪\n" +
                    "以暴力、胁迫或者其他方法强制猥亵他人或者侮辱妇女的，处五年以下有期徒刑或者拘役。 聚众或者在公共场所当众犯前款罪的，或者有其他恶劣情节的，处五年以上有期徒刑。 猥亵儿童的，依照前两款的规定从重处罚。"};
    public static final String[] QieLU = {"切噜", "切哩", "切吉", "噜拉", "啪噜", "切璐", "扣", "啦哩", "啦嘟", "切泼", "啪噼", ",", "嚕嚕", "啰哩", "切拉", "切噼"};
    public static final String ExcelDir = "Excel/";
    public static final String fileTimeFormat = "yy年MM月dd日";
    public static final DateTimeFormatter df = DateTimeFormatter.ofPattern(fileTimeFormat);
    public static String robotQQ = "0";//机器人qq
    public static String[] one = {"日和莉", "怜", "未奏希", "胡桃", "依里", "由加莉", "铃莓", "碧", "美咲", "莉玛"};
    public static String[] two = {"茜里", "宫子", "雪", "铃奈", "香织", "美美", "惠理子", "忍", "真阳", "栞", "千歌", "空花", "珠希", "美冬", "深月", "铃", "绫音", "美里"};
    public static String[] Three = {"杏奈", "真步", "璃乃", "初音", "依绪", "咲恋", "望", "妮胧", "秋乃", "真琴", "静流", "莫妮卡", "姬塔", "纯", "亚里莎", "冰川镜华"};
    public static String[] noUpThree = {"杏奈", "真步", "璃乃", "依绪", "望", "妮胧", "秋乃", "真琴", "静流", "莫妮卡", "姬塔", "纯", "咲恋", "冰川镜华"};
    public static String[] noUptwo = {"紡希", "绫音", "茜里", "宫子", "雪", "铃奈", "香织", "美美", "惠理子", "忍", "真阳", "栞", "千歌", "空花", "珠希", "美冬", "深月", "铃"};
    public static String[] noUpone = {"胡桃", "铃莓", "日和莉", "怜", "未奏希", "依里", "由加莉", "美咲", "莉玛", "茉莉"};
    public static String[] Three_plus = {"初音"};
    public static String[] two_plus = {"美里"};
    public static String[] one_plus = {"碧"};
    public static int ThreeChance = 25;
    public static int TwoChance = 180;
    public static int OneChance = 795;
    public static int baijinThreeChance = 25;
    public static int baijiTwoChance = 180;
    public static int baijiOneChance = 795;
    public static int upThreeChance = 7;
    public static int upTwoChance = 30;//30;
    public static int upOneChance = 100;//200;
    public static HashMap<String, Integer> reQieLU = new HashMap<>();
    public static boolean canSendImage = false;//这个机器人能不能发送图片的标记
    public static String ip;
    public static PricnessConfig pricnessConfig;
    public static String[] emojis = new String[]{"\uD83E\uDD84", "\uD83D\uDC34", "\uD83D\uDC3A", "\uD83D\uDC02", "\uD83D\uDC04", "\uD83D\uDC0E", "\uD83D\uDC07", "\uD83D\uDC13", "\uD83E\uDD8F", "\uD83D\uDC29", "\uD83D\uDC2E", "\uD83D\uDC35", "\uD83D\uDC19", "\uD83D\uDC80", "\uD83D\uDC24", "\uD83D\uDC28", "\uD83D\uDC2E", "\uD83D\uDC14", "\uD83D\uDC38", "\uD83D\uDC7B", "\uD83D\uDC1B", "\uD83D\uDC20", "\uD83D\uDC36", "\uD83D\uDC2F", "  ", "\uD83D\uDEBD"};
    public static HoreEvent horeEvent;


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

    //加载配置文件
    public static void getFile() {
        File file = new File("config.txt");
        //群组设定
        if (!file.exists() || !file.isFile()) {
            //没有读取到配置文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //读取到了
            String jsonString;
            try {
                jsonString = FileUtils.readFileToString(file, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(jsonString);

                Set<String> keySet = jsonObject.keySet();
                for (String s : keySet) {
                    String string = jsonObject.getJSONObject(s).toJSONString();
                    groupPower keyValues = JSONArray.parseObject(string, groupPower.class);
                    On.put(s, keyValues);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
            }
        }
    }

    public static void getconfig() {
        //通用设定
        File file = new File("通用配置.txt");
        Properties pro = new Properties();
        OutputStreamWriter op = null;
        if (!file.exists() || !file.isFile()) {
            //没有读取到配置文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //生成一个初始文件
            pro.setProperty("提醒买药小助手图片名", "抽卡.png");
            pro.setProperty("抽卡上限", "1000");
            pro.setProperty("抽卡冷却秒", "30");
            pro.setProperty("总开关默认开启", "true");
            pro.setProperty("抽奖默认开启", "true");
            pro.setProperty("抽卡默认开启", "true");
            pro.setProperty("赛马默认开启", "true");
            pro.setProperty("主人qq", "12345");
            pro.setProperty("发一次色图花费", "500");
            pro.setProperty("签到一次金币", "2000");

            pricnessConfig = new PricnessConfig("抽卡.png", 1000, 30, true, true, true, true, "12345", 2000, 500);

            try {
                op = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
                pro.store(op, "the PcrTool configs");

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    op.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //读取到了
            InputStreamReader in = null;
            try {
                in = new InputStreamReader(new FileInputStream(file), "utf-8");
                pro.load(in);
                pricnessConfig = new PricnessConfig(pro.getProperty("提醒买药小助手图片名"),
                        Integer.parseInt(pro.getProperty("抽卡上限")),
                        Integer.parseInt(pro.getProperty("抽卡冷却秒")),
                        Boolean.parseBoolean(pro.getProperty("总开关默认开启")),
                        Boolean.parseBoolean(pro.getProperty("抽奖默认开启")),
                        Boolean.parseBoolean(pro.getProperty("抽卡默认开启")),
                        Boolean.parseBoolean(pro.getProperty("赛马默认开启")),
                        pro.getProperty("主人qq"),
                        Integer.parseInt(pro.getProperty("签到一次金币")),
                        Integer.parseInt(pro.getProperty("发一次色图花费"))
                );
            } catch (Exception e) {
                pricnessConfig = new PricnessConfig("抽卡.png", 1000, 30, true, true, true, true, "12345", 2000, 500);//没读到就生成一个新的
                try {
                    op = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
                    pro.store(op, "the PcrTool configs");

                } catch (IOException e1) {
                    e.printStackTrace();
                } finally {
                    try {
                        op.close();
                    } catch (IOException e2) {
                        e.printStackTrace();
                    }
                }
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //刷新写入配置文件
    public synchronized static void setjson() {
        String jsonObject = JSONObject.toJSONString(On);
        try {
            File file = new File("config.txt");
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
            }
            FileUtils.write(file, jsonObject, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读好坏事
    public synchronized static void getEvent() {
        horeEvent = new HoreEvent();
        String jsonObject = JSONObject.toJSONString(horeEvent);
        try {
            File file = new File("事件.txt");
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
                FileUtils.write(file, jsonObject, "utf-8");
            } else {
                JSONObject jsonObject1 = JSONObject.parseObject(FileUtils.readFileToString(file, "utf-8"));
                List bedHorseEvent = JSONArray.parseObject(jsonObject1.get("bedHorseEvent").toString(), List.class);
                List goodHorseEvent = JSONArray.parseObject(jsonObject1.get("goodHorseEvent").toString(), List.class);
                horeEvent.getGoodHorseEvent().addAll(goodHorseEvent);
                horeEvent.getBedHorseEvent().addAll(bedHorseEvent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读扭蛋信息
    public synchronized static void getgachi() {
        try {
            File file = new File("扭蛋.txt");
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
                //准备内置的转蛋信息写入内存
                //up池
                JSONObject upgachi = new JSONObject();
                upgachi.put("三星总概率", ThreeChance);
                upgachi.put("二星总概率", TwoChance);
                upgachi.put("一星总概率", OneChance);
                upgachi.put("三星人物池（除去up角）", noUpThree);
                upgachi.put("二星人物池（除去up角）", noUptwo);
                upgachi.put("一星人物池（除去up角）", noUpone);
                upgachi.put("三星人物池（up角）", Three_plus);
                upgachi.put("二星人物池（up角）", two_plus);
                upgachi.put("一星人物池（up角）", one_plus);
                upgachi.put("三星up总概率", upThreeChance);
                upgachi.put("二星up总概率", upTwoChance);
                upgachi.put("一星up总概率", upOneChance);

                //白金池
                JSONObject gachi = new JSONObject();
                gachi.put("三星总概率", baijinThreeChance);
                gachi.put("二星总概率", baijiTwoChance);
                gachi.put("一星总概率", baijiOneChance);
                gachi.put("三星人物池", Three);
                gachi.put("二星人物池", two);
                gachi.put("一星人物池", one);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("up池信息", upgachi);
                jsonObject.put("白金池信息", gachi);

                FileUtils.write(file, jsonObject.toJSONString(), "utf-8");

            } else {
                JSONObject jsonObject = JSONObject.parseObject(FileUtils.readFileToString(file, "utf-8"));
                JSONObject gachi = (JSONObject) jsonObject.get("白金池信息");
                JSONObject upgachi = (JSONObject) jsonObject.get("up池信息");
                baijinThreeChance = (int) gachi.get("三星总概率");
                baijiTwoChance = (int) gachi.get("二星总概率");
                baijiOneChance = (int) gachi.get("一星总概率");
                Three = ((JSONArray) gachi.get("三星人物池")).toArray(new String[0]);
                two = ((JSONArray) gachi.get("二星人物池")).toArray(new String[0]);
                one = ((JSONArray) gachi.get("一星人物池")).toArray(new String[0]);


                ThreeChance = (int) upgachi.get("三星总概率");
                TwoChance = (int) upgachi.get("二星总概率");
                OneChance = (int) upgachi.get("一星总概率");
                noUpThree = ((JSONArray) upgachi.get("三星人物池（除去up角）")).toArray(new String[0]);
                noUptwo = ((JSONArray) upgachi.get("二星人物池（除去up角）")).toArray(new String[0]);
                noUpone = ((JSONArray) upgachi.get("一星人物池（除去up角）")).toArray(new String[0]);
                Three_plus = ((JSONArray) upgachi.get("三星人物池（up角）")).toArray(new String[0]);
                two_plus = ((JSONArray) upgachi.get("二星人物池（up角）")).toArray(new String[0]);
                try {
                    one_plus = ((JSONArray) upgachi.get("一星人物池（up角）")).toArray(new String[0]);
                } catch (NullPointerException e) {
                }
                upThreeChance = (int) upgachi.get("三星up总概率");
                upTwoChance = (int) upgachi.get("二星up总概率");
                try {
                    upOneChance = (int) upgachi.get("一星up总概率");
                } catch (NullPointerException e) {
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("扭蛋配置文件错误，是否删除了一项？");
        }
    }

}
