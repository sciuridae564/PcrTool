package cn.sciuridae.listener;

import cn.sciuridae.dataBase.bean.*;
import cn.sciuridae.dataBase.service.*;
import cn.sciuridae.utils.ExcelWrite;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMemberReduce;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.utils.stringTool.*;
import static cn.sciuridae.utils.timeUtil.getDescDateList;
import static cn.sciuridae.utils.timeUtil.getTodayFive;

@Service
public class prcnessListener {

    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    KnifeListService knifeListServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    PcrUnionService pcrUnionServiceImpl;

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#未出刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void searchVoidKnife(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        HashMap<String, Integer> map;
        StringBuilder src;
        src = new StringBuilder();
        if (strings != null && strings.size() > 0) {
            //有@人的
            map = new HashMap<>();
            for (String QQ : strings) {
                map.put(QQ, 3 - knifeListServiceImpl.getKnifeNum(Long.parseLong(QQ), LocalDateTime.now(), true));
            }
        } else {
            //工会全体
            map = new HashMap<>();
            List<Long> longs = teamMemberServiceImpl.getTeamMemberQQByGroup(msg.getGroupCodeNumber());
            for (long l : longs) {
                map.put(String.valueOf(l), 3 - knifeListServiceImpl.getKnifeNum(l, LocalDateTime.now(), true));
            }
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
    @Filter(value = "#已出刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void searchKnife(GroupMsg msg, MsgSender sender) {
        List<KnifeList> list = new ArrayList<>();
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        StringBuilder stringBuilder = new StringBuilder();
        if (strings != null && strings.size() > 0) {
            //找 人的出刀
            for (String s : strings) {
                list.addAll(knifeListServiceImpl.getKnife(cqAtoNumber(s), LocalDateTime.now()));
            }
        } else {
            //找整个工会的出刀
            list = knifeListServiceImpl.getKnifeList(msg.getGroupCodeNumber(), LocalDateTime.now());
        }
        if (list.size() > 0) {
            stringBuilder.append("出刀信息：");
            for (KnifeList knife : list) {
                stringBuilder.append("\n-----\n编号: ").append(knife.getId());
                stringBuilder.append("\n昵称：").append(teamMemberServiceImpl.getName(knife.getKnifeQQ()));
                stringBuilder.append("\n扣扣：").append(knife.getKnifeQQ());
                stringBuilder.append("\n伤害：").append(knife.getHurt());
                stringBuilder.append("\n").append(knife.getLoop());
                stringBuilder.append("-").append(knife.getPosition());
            }
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), stringBuilder.toString());
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "还没有刀信息哦");
        }

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#出刀"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void getKnife(GroupMsg msg, MsgSender sender) {
        if (!teamMemberServiceImpl.getGroupByQQ(msg.getQQCodeNumber()).equals(msg.getGroupCodeNumber())) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "¿,他群间谍发现，建议rbq一周");
        } else {
            try {
                int i = ProgressServiceImpl.isFight(msg.getGroupCodeNumber());
                try {
                    Tree tree = new Tree();
                    tree.setDate(LocalDateTime.now());
                    tree.setGroupQQ(msg.getGroupCodeNumber());
                    tree.setTeamQQ(msg.getQQCodeNumber());
                    tree.setName(teamMemberServiceImpl.getName(msg.getQQCodeNumber()));
                    tree.setTree(false);
                    treeServiceImpl.save(tree);
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "出刀已记录，@会长看我表演");
                } catch (UncategorizedSQLException e) {
                    if (e.getSQLException().getErrorCode() == 19) {
                        //主键冲突，
                        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已经在出刀/挂树状态");
                    }
                }
            } catch (BindingException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没有开启工会战惹");
            }
        }

    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#挂树", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void getTree(GroupMsg msg, MsgSender sender) {
        try {
            if (!teamMemberServiceImpl.getGroupByQQ(msg.getQQCodeNumber()).equals(msg.getGroupCodeNumber())) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "¿,他群间谍发现，建议rbq一周");
            } else {
                int i = treeServiceImpl.updateTree(msg.getQQCodeNumber());
                if (i == 1) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), isTree);
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有在树上");
                }
            }
        } catch (NullPointerException e) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没有加入工会");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#收刀", "#交刀"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void outKnife(GroupMsg msg, MsgSender sender) {
        try {
            int hurt = getHurt(msg.getMsg(), 1);

            sender.SENDER.sendGroupMsg(msg.getGroupCode(), toHurt(msg.getGroupCodeNumber(), msg.getQQCodeNumber(), hurt));
        } catch (NumberFormatException e) {
            //伤害数字转换失败
            e.printStackTrace();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), commandError);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#开始会战", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void startFight(GroupMsg msg, MsgSender sender) {
        try {
            if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
                if (ProgressServiceImpl.isFight(msg.getGroupCodeNumber()) != null) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), StartFightStartDouble);
                    return;
                }

                String time = msg.getMsg().replaceAll(" +", "").substring(5);
                LocalDateTime startTime = getTodayFive(LocalDateTime.now());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                if (!time.equals("")) {
                    LocalDate localDate = LocalDate.parse(time, df);
                    startTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
                }
                ;
                Progress progress = new Progress();
                progress.setLoop(1);
                progress.setSerial(1);
                progress.setRemnant(BossHpLimit[0]);
                progress.setTeamQQ(msg.getGroupCodeNumber());
                progress.setVersion(1);
                progress.setStartTime(startTime);
                progress.setEndTime(startTime.plusDays(8));

                ProgressServiceImpl.save(progress);
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "成功记录会战\n" + startTime.format(df) + "到" + startTime.plusDays(8).format(df));
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "日期格式错误，示例 2020-05-04");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#结束会战", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void endFight(GroupMsg msg, MsgSender sender) {
        if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            Progress progress = ProgressServiceImpl.getProgress(msg.getGroupCodeNumber());
            if (progress == null) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), EngFightStartDouble);
                return;
            }

            ProgressServiceImpl.removeById(progress.getId());
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), SuccessEndFight);
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    //撤刀 撤回刀的编号
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#撤刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void dropKnife(GroupMsg msg, MsgSender sender) {
        if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            try {
                int id = Integer.valueOf(msg.getMsg().replaceAll(" +", "").substring(2));
                knifeListServiceImpl.removeById(id);
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "操作成功");
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), commandError);
            } catch (Exception e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "删除失败");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    //调整boss状态 周目 几王 血量
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#调整boss状态", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void changeBoss(GroupMsg msg, MsgSender sender) {
        if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            String[] change = msg.getMsg().replaceAll(" +", " ").split(" ");
            boolean is = true;
            Progress progress = ProgressServiceImpl.getProgress(msg.getGroupCodeNumber());
            int loop = Integer.valueOf(change[1]);
            int serial = Integer.valueOf(change[2]);
            is = loop == progress.getLoop() && serial == progress.getSerial();
            progress.setLoop(loop);
            progress.setSerial(serial);
            progress.setRemnant(Integer.valueOf(change[3]));

            ProgressServiceImpl.updateFight(progress);
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
    @Filter(value = "#代刀", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void sideKnife(GroupMsg msg, MsgSender sender) {
        if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
            int hurt = getHurt(msg.getMsg(), 2);
            long qq = cqAtoNumber(strings.get(0));

            sender.SENDER.sendGroupMsg(msg.getGroupCode(), toHurt(msg.getGroupCodeNumber(), qq, hurt));
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#查树", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void searchTree(GroupMsg msg, MsgSender sender) {
        List<Tree> trees = treeServiceImpl.getTreeByGroup(msg.getGroupCodeNumber());

        if (trees.size() == 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "无 人 挂 树");
        } else {
            StringBuilder stringBuilder = new StringBuilder("挂树名单:");
            for (Tree tree : trees) {
                stringBuilder.append("\n[CQ:at,qq=").append(tree.getTeamQQ()).append("] ");
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
        }
    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#正在出刀", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void searchOutKnife(GroupMsg msg, MsgSender sender) {
        List<Tree> trees = treeServiceImpl.getFightByGroup(msg.getGroupCodeNumber());

        if (trees.size() == 0) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "无 人 出 刀");
        } else {
            StringBuilder stringBuilder = new StringBuilder("正在出刀:");
            for (Tree tree : trees) {
                stringBuilder.append("\n[CQ:at,qq=").append(tree.getTeamQQ()).append("] ");
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"boss状态", "boss", "boss咋样了", "boss还好吗"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void getBoss(GroupMsg msg, MsgSender sender) {
        Progress progress = ProgressServiceImpl.getProgress(msg.getGroupCodeNumber());
        if (progress != null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "现在为boss为" + progress.getLoop() + "-" + progress.getSerial() + "\n剩余血量："
                    + progress.getRemnant());
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notBossOrNotDate);
        }
    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#生成excel"}, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void getExcel(GroupMsg msg, MsgSender sender) {
        String[] cmd = msg.getMsg().split(" +");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ArrayList<LocalDate> arrayList = new ArrayList();
        try {
            if (cmd.length == 2) {
                arrayList.add(LocalDate.parse(cmd[1], formatter));
            } else if (cmd.length == 3) {
                arrayList.addAll(getDescDateList(LocalDate.parse(cmd[1], formatter), LocalDate.parse(cmd[2], formatter)));
            } else if (cmd.length == 1) {
                arrayList.add(LocalDate.now());
            }
        } catch (DateTimeParseException e) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "日期格式错误");
            return;
        }

        //验证有没有工会
        PcrUnion group = pcrUnionServiceImpl.getGroup(msg.getGroupCodeNumber());
        if (group != null) {
            try {
                String groupQQ = msg.getGroupCode();//工会qq
                ExcelWrite excelWrite;
                if (arrayList.size() > 1) {
                    excelWrite = new ExcelWrite(getExcelFileName(groupQQ, arrayList.get(0), arrayList.get(arrayList.size() - 1)), arrayList, msg.getGroupCodeNumber());
                } else {
                    excelWrite = new ExcelWrite(getExcelFileName(groupQQ, arrayList.get(0)), arrayList, msg.getGroupCodeNumber());
                }

                excelWrite.writedDate();
                excelWrite.reflashFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), noThisGroup);
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"获取码"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void getToken(PrivateMsg msg, MsgSender sender) {
        String QQ = msg.getQQCode();

        sender.SENDER.sendPrivateMsg(QQ, "你的码是：" + teamMemberServiceImpl.getToken(msg.getQQCodeNumber()));
        sender.SENDER.sendPrivateMsg(QQ, "会战后台网址：http://" + ip + "/8080");

    }


    @Listen(MsgGetTypes.groupMemberReduce)
    public void deleteTeam(GroupMemberReduce msg, MsgSender sender) {
        String QQ = msg.getBeOperatedQQ();
        Long qq = Long.parseLong(QQ);
        TeamMember teamMember = teamMemberServiceImpl.getTeamMemberByQQ(qq);
        if (teamMember.getGroupQQ() == msg.getGroupCodeNumber()) {
            teamMemberServiceImpl.removeById(teamMember);//删除记录
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), teamMember.getName() + " 离开了我们");
        }
    }

    //处理交刀的数据
    public String toHurt(long groupQQ, long QQ, int hurt) {
        StringBuilder stringBuilder = new StringBuilder();
        Progress progress = ProgressServiceImpl.getProgress(groupQQ);
        KnifeList knifeList;
        if (progress != null) {//没有工会boss进度数据
            if (progress.getStartTime().isBefore(LocalDateTime.now())) { //时间若已过开始则可以上报伤害
                //会战开启 上报伤害
                //查询这个人出没出完三刀
                if (knifeListServiceImpl.getKnifeNum(QQ, LocalDateTime.now(), true) > 2) {
                    return "三刀已出完，不可再出了";
                }

                try {
                    treeServiceImpl.removeById(QQ);
                } catch (Exception e) {
                }

                knifeList = new KnifeList();//创建刀数据对象
                knifeList.setKnifeQQ(QQ);
                knifeList.setLoop(progress.getLoop());
                knifeList.setPosition(progress.getSerial());
                knifeList.setDate(LocalDateTime.now());
                //计算boss血量，分成打爆处理（有救树流程）和没打爆处理

                if (progress.getRemnant() - hurt > 0) {
                    stringBuilder.delete(0, stringBuilder.length());
                    knifeList.setComplete(true);
                    knifeList.setHurt(hurt);
                    progress.setRemnant(progress.getRemnant() - hurt);
                    //没打穿boss
                } else {
                    //伤害打穿了，进入下一模式
                    int loop = (progress.getSerial() == 5 ? progress.getLoop() + 1 : progress.getLoop());
                    int serial = (progress.getSerial() == 5 ? 1 : progress.getSerial() + 1);
                    progress.setLoop(loop);
                    progress.setLoop(serial);
                    progress.setRemnant(BossHpLimit[serial - 1]);
                    knifeList.setHurt(progress.getRemnant());//伤害值为boss血量

                    //进入救树模式，把树上的人都噜下来
                    List<Tree> strings = treeServiceImpl.deletTreeByGroup(groupQQ);

                    List<KnifeList> strins = knifeListServiceImpl.getKnife(QQ, LocalDateTime.now());
                    //判断是不是补时刀
                    if (strins.size() != 0 && !strins.get(strins.size() - 1).getComplete()) {
                        knifeList.setComplete(true);
                    } else {
                        knifeList.setComplete(false);
                    }
                    if (strings.size() > 0) {
                        stringBuilder.append("下树啦，下树啦");
                        for (Tree tree : strings) {
                            stringBuilder.append("[CQ:at,qq=").append(tree.getTeamQQ()).append("] ");
                        }
                    }
                    //这刀打爆了boss
                }
                //更新数据库数据
                ProgressServiceImpl.updateById(progress);
                knifeListServiceImpl.save(knifeList);

                stringBuilder.append("现在boss状态:").append(progress.getLoop()).append("-").append(progress.getSerial()).append("\n");
                stringBuilder.append("血量：").append(progress.getRemnant());
            } else {
                //会战还未开启
                Duration duration = Duration.between(progress.getStartTime(), LocalDateTime.now());
                stringBuilder.append("会战将在").append(duration.toDays()).append("日").append(duration.toHours()).append("时后开启，还没有到打boss的时候哦");
            }
        } else {
            return "还没开始为什么就交刀惹";
        }
        return stringBuilder.toString();
    }

}