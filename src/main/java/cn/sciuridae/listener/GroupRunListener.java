package cn.sciuridae.listener;

import cn.sciuridae.dataBase.bean.PcrUnion;
import cn.sciuridae.dataBase.bean.TeamMember;
import cn.sciuridae.dataBase.service.PcrUnionService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.utils.stringTool.cqAtoNumber;
import static cn.sciuridae.utils.stringTool.getVar;

@Service
public class GroupRunListener {

    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    PcrUnionService pcrUnionServiceImpl;

    /**
     * 格式 #建会@机器人 工会名 自己的游戏名字（默认创建者为工会长）
     *
     * @param msg
     * @param sender
     */
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#建会", at = true, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void createGroup(GroupMsg msg, MsgSender sender) {

        String[] strings = msg.getMsg().split(" +");
        //参数少于4个
        if (strings.length > 4) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "参数少于指定，是否少了空格？");
            return;
        }

        //已经有一个工会了
        if (teamMemberServiceImpl.getTeamMemberByQQ(msg.getQQCodeNumber()) != null) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getName() + isHaveGroup);
            return;
        }

        //准备工会和人员信息
        PcrUnion pcrUnion = new PcrUnion();
        pcrUnion.setCreateDate(LocalDateTime.now());
        pcrUnion.setGroupQQ(msg.getGroupCodeNumber());
        pcrUnion.setGroupName(strings[2]);//工会名字
        pcrUnion.setGroupMasterQQ(msg.getQQCodeNumber());
        pcrUnion.setTeamSum(1);

        TeamMember teamMember = new TeamMember();
        teamMember.setGroupQQ(msg.getGroupCodeNumber());
        teamMember.setUserQQ(msg.getQQCodeNumber());
        teamMember.setName(strings[3]);
        teamMember.setPower(true);
        pcrUnionServiceImpl.save(pcrUnion);
        teamMemberServiceImpl.save(teamMember);

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), strings[2] + " 工会已经创建好辣 [CQ:at,qq=" + msg.getQQCode() + "]");


    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#批量入会", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void getGroups(GroupMsg msg, MsgSender sender) {
        //先看下是不是管理员
        if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
            int num = pcrUnionServiceImpl.getVoidSize(msg.getGroupCodeNumber());
            if (num + strings.size() <= 30) {
                ArrayList<Long> have = new ArrayList<>();
                for (String s : strings) {
                    TeamMember teamMember = new TeamMember(cqAtoNumber(s), msg.getGroupCodeNumber(), sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getName(), false);
                    try {
                        teamMemberServiceImpl.save(teamMember);
                        num++;
                    } catch (UncategorizedSQLException e) {
                        if (e.getSQLException().getErrorCode() == 19) {
                            //主键冲突，就是qq号已经在库里有了
                            have.add(cqAtoNumber(s));
                        }
                    }
                }
                pcrUnionServiceImpl.updateVoidSize(msg.getGroupCodeNumber());//更新空位信息
                StringBuilder stringBuilder = new StringBuilder();
                if (have.size() > 0) {
                    stringBuilder.append("已经有工会的人：");
                    for (long s : have) {
                        stringBuilder.append(cqCodeUtil.getCQCode_At(String.valueOf(s)));
                    }

                }
                stringBuilder.append("指令完成惹");
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
                return;
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "会里空位没那么多惹");
            return;
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#入会", at = true, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void getGroup(GroupMsg msg, MsgSender sender) {
        String name = getVar(msg.getMsg());

        TeamMember teamMember = new TeamMember(msg.getQQCodeNumber(),
                msg.getGroupCodeNumber(),
                name.trim().equals("") ? sender.GETTER.getGroupMemberInfo(msg.getGroupCode(), msg.getQQCode()).getName() : name,
                false);
        int num;
        try {
            num = pcrUnionServiceImpl.getVoidSize(msg.getGroupCodeNumber());
        } catch (BindingException e) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), noThisGroup);
            return;
        }

        if (num == 30) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), isFullGroup);//工会满员
            return;
        }

        try {
            teamMemberServiceImpl.save(teamMember);
            pcrUnionServiceImpl.updateVoidSize(msg.getGroupCodeNumber());//更新空位信息
        } catch (UncategorizedSQLException e) {

            if (e.getSQLException().getErrorCode() == 19) {
                //主键冲突，就是qq号已经在库里有了
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), isHaveGroup);//已有工会
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "数据库写入异常， 这样被注入,不行♥");
            }
            return;
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), successJoinGroup);//成功加入
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#退会", at = true, keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void outGroup(GroupMsg msg, MsgSender sender) {

        if (pcrUnionServiceImpl.isGroupMaster(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            //会长退出销毁工会
            teamMemberServiceImpl.deleteTeamMemberByGroup(msg.getGroupCodeNumber());
            pcrUnionServiceImpl.deleteGroup(msg.getGroupCodeNumber());
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), successDropGroup);
        } else {
            if (teamMemberServiceImpl.removeById(msg.getQQCodeNumber())) {
                pcrUnionServiceImpl.updateVoidSize(msg.getGroupCodeNumber());//更新空位信息
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), successOutGroup);
            }
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#转让会长", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void changeSuperPower(GroupMsg msg, MsgSender sender) {
        if (!pcrUnionServiceImpl.isGroupMaster(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有本群工会的会长权限");
            return;
        }

        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (strings.size() != 1) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "错误：指令@的人不等于1个");
            return;
        }
        long newQQ = cqAtoNumber(strings.get(0));
        System.out.println(newQQ);
        Long group = teamMemberServiceImpl.getGroupByQQ(newQQ);
        Long groupByQQ = teamMemberServiceImpl.getGroupByQQ(msg.getQQCodeNumber());
        if (!groupByQQ.equals(group)) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "她还不在这个工会\n答应我，不要做舔狗好吗/委屈");
            return;
        }
        //转移工会会长权限
        pcrUnionServiceImpl.changeGroupMaster(group, newQQ);
        teamMemberServiceImpl.setAdmin(newQQ);

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), successChangeSuperPower);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#撤下管理", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void downAdmin(GroupMsg msg, MsgSender sender) {
        if (!pcrUnionServiceImpl.isGroupMaster(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有本群工会的会长权限");
            return;
        }

        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (strings.size() != 1) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "错误：指令@的人不等于1个");
            return;
        }
        long newQQ = cqAtoNumber(strings.get(0));
        Long group = teamMemberServiceImpl.getGroupByQQ(newQQ);
        Long groupByQQ = teamMemberServiceImpl.getGroupByQQ(msg.getQQCodeNumber());
        if (!groupByQQ.equals(group)) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "她还不在这个工会\n");
            return;
        }

        teamMemberServiceImpl.deAdmin(newQQ);
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "撤除管理成功");

    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#改名", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void reName(GroupMsg msg, MsgSender sender) {
        String newName = msg.getMsg();
        Long traget;
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (strings.size() < 1) {
            //没有at人，给自己改名
            newName = newName.replaceAll(" +", "");
            newName = newName.substring(3);
            traget = msg.getQQCodeNumber();
        } else {
            //给别人改名
            if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
                traget = cqAtoNumber(strings.get(0));
                newName = newName.substring(newName.indexOf("]") + 1).trim();
            } else {
                sender.SENDER.sendPrivateMsg(msg.getQQCode(), "无权限");
                return;
            }

        }


        if (newName.length() > 20) {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "那么长的名字，本小姐记不住呢");
            return;
        }
        Integer integer = teamMemberServiceImpl.setName(traget, newName);
        if (integer < 1) {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "还没有进入任何一个工会惹");
        } else {
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "改名成功，现在名称为：" + newName);
        }


    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#改工会名", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void reGroupName(GroupMsg msg, MsgSender sender) {
        if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            String newName = msg.getMsg();
            newName = newName.replaceAll(" +", "");
            newName = newName.substring(5);
            pcrUnionServiceImpl.changeUnionName(msg.getGroupCodeNumber(), newName);
            sender.SENDER.sendPrivateMsg(msg.getQQCode(), "改名成功，现在名称为：" + newName);
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "工会信息", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void GroupNameStatue(GroupMsg msg, MsgSender sender) {
        PcrUnion pcrUnion = pcrUnionServiceImpl.getGroup(msg.getGroupCodeNumber());
        String stringBuilder = "工会名：" + pcrUnion.getGroupName() +
                "\n工会会长qq：" + pcrUnion.getGroupMasterQQ() +
                "\n工会创建时间：" + pcrUnion.getCreateDate() +
                "\n工会成员数：" + pcrUnion.getTeamSum();
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "工会成员列表", keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void GroupMemberList(GroupMsg msg, MsgSender sender) {
        StringBuilder stringBuilder = new StringBuilder();

        List<TeamMember> teamMembers = teamMemberServiceImpl.getTeamMemberByGroup(msg.getGroupCodeNumber());

        long groupMaster = pcrUnionServiceImpl.getGroupMaster(msg.getGroupCodeNumber());
        if (teamMembers != null) {
            stringBuilder.append("工会成员:\n");
            for (TeamMember teamMember : teamMembers) {
                stringBuilder.append("qq：").append(teamMember.getUserQQ());
                stringBuilder.append("  昵称：").append(teamMember.getName());
                if (teamMember.getPower()) {
                    if (teamMember.getUserQQ() == groupMaster) {
                        stringBuilder.append("  会长 \n");
                    } else {
                        stringBuilder.append("  管理员 \n");
                    }
                } else {
                    stringBuilder.append("\n");
                }
            }
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
        } else {
            stringBuilder.append("还没有创建工会哦");
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
        }
    }


    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"设置管理.*", "设置管理员.*", "添加管理员.*", "添加管理.*"})
    public void setAdmin(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
        if (pcrUnionServiceImpl.isGroupMaster(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {

            long QQ = cqAtoNumber(strings.get(0));
            long group = teamMemberServiceImpl.getGroupByQQ(QQ);
            if (group == msg.getGroupCodeNumber()) {
                long l = teamMemberServiceImpl.setAdmin(QQ);
                if (l < 1) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), noFindTheOne);
                } else {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "成功设置管理员");
                }
            } else {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "ta不在这个工会");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#踢人", keywordMatchType = KeywordMatchType.TRIM_STARTS_WITH)
    public void kickman(GroupMsg msg, MsgSender sender) {
        int num = 0;
        if (teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
            long deleteQQ;
            if (strings.size() > 0) {
                deleteQQ = cqAtoNumber(strings.get(0));
            } else {
                deleteQQ = Long.parseLong(msg.getMsg().substring(3).trim());
            }
            ;//要被踢掉的那个人
            if (pcrUnionServiceImpl.isGroupMaster(deleteQQ, msg.getGroupCodeNumber())) {
                num = -2;
            } else {
                num = teamMemberServiceImpl.removeById(deleteQQ) ? 1 : 0;
            }
            if (num == 0) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "没有踢掉任何一个人，@的这个人是不是还没有加入这个工会呢");
            } else if (num == -2) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "堂下何人竟敢状告本官");
                if (pcrUnionServiceImpl.isGroupMaster(msg.getQQCodeNumber(), msg.getGroupCodeNumber())) {
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "阿，竟是中堂大人，失敬失敬。\nだが、断る，想解散用退会阿");
                }
            } else {
                pcrUnionServiceImpl.updateVoidSize(msg.getGroupCodeNumber());//更新空位信息
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "成功踢掉了");
            }
        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), notPower);
        }
    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = "更换token", keywordMatchType = KeywordMatchType.EQUALS)
    public void startHorse(PrivateMsg msg, MsgSender sender) {
        String token;
        do {
            token = RandomStringUtils.randomAlphanumeric(20);//密匙生成
            try {
                Integer tokenNum = teamMemberServiceImpl.getTokenNum(token);
                if (tokenNum < 1)
                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } while (true);
        teamMemberServiceImpl.updateToken(msg.getQQCodeNumber(), token);
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "新的码为: " + token);
    }
}
