package cn.sciuridae.Listener;

import cn.sciuridae.bean.FightStatue;
import cn.sciuridae.bean.Group;
import cn.sciuridae.bean.Knife;
import cn.sciuridae.bean.teamMember;
import cn.sciuridae.sqLite.DB;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.sciuridae.Tools.stringTool.getAtNumber;
import static cn.sciuridae.Tools.stringTool.getVar;
import static cn.sciuridae.Tools.stringTool.searchAtNumber;
import static cn.sciuridae.constant.*;

@Beans
public class prcnessListener {

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#帮助.*" ,at = true)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), helpMsg);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#建会.*" ,at = true)
    public void createGroup(GroupMsg msg, MsgSender sender){
        robotQQ= msg.getThisCode();
        String str=msg.getMsg();
        str.replaceAll(" ", "");
        int atNum=searchAtNumber(str);
        String groupMasterQQ;
        String groupName;
        if(atNum>2){
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), helpMsg);
            return;
        }else if(atNum==1){//只有at了机器人
            groupMasterQQ=msg.getQQ();
        }else {//有at其他人
            groupMasterQQ=getAtNumber(str,robotQQ);
        }
        System.out.println(msg);
        groupName=str.substring(3).replaceAll("\\[.*]","");
        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        Group group=new Group();
        group.setCreateDate(df.format(date));
        group.setGroupid(msg.getGroupCode());
        group.setGroupName(groupName);
        group.setGroupMasterQQ(groupMasterQQ);

        //System.out.println(group);
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#入会.*" ,at = true)
    public void getGroup(GroupMsg msg, MsgSender sender){
        teamMember teamMember=new teamMember(msg.getQQCode(),false,null, getVar(msg.getMsg()));

        switch (DB.Instance.joinGroup(teamMember,msg.getGroupCode())){
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
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), isFullGroup);//成功加入
                break;
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#离会.*" ,at = true)
    public void outGroup(GroupMsg msg, MsgSender sender){
        switch (DB.Instance.outGroup(msg.getQQCode())){
            case -1:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), error);
                break;
            case 0:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), successOutGroup);
                break;
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#查刀.*"  )
    public void searchVoidKnife(GroupMsg msg, MsgSender sender){

        sender.SENDER.sendGroupMsg(msg.getGroupCode(), DB.getInstance().searchVoidKnife(msg.getGroupCode()));
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#出刀.*" )
    public void getKnife(GroupMsg msg, MsgSender sender){
        FightStatue fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
        if(fightStatue!=null){
            int no=fightStatue.getLoop()*10+fightStatue.getSerial();
            if(fightStatue.getRemnant()==-1){
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), "怪物还没有录入血量惹，快上游戏看一下鸭");
                return;
            }
            switch (DB.getInstance().joinTree(msg.getQQCode(),no,msg.getGroupCode())){
                case -1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(), "好像出了点什么状况");
                    break;
                case 1:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(),"¿,他群间谍发现，建议rbq一周" );
                    break;
                case 2:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(),"¿,打咩，没有第二棵树能上了" );
                    break;
                case 3:
                    sender.SENDER.sendGroupMsg(msg.getGroupCode(),"已挂东南枝" );
                    break;
            }
        }else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没有开启工会战惹");
        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#上树.*" )
    public void getTree(GroupMsg msg, MsgSender sender){
        switch (DB.Instance.trueTree(msg.getQQCode(),msg.getGroupCode())){
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
    @Filter(value = "#交刀.*" )
    public void outKnife(GroupMsg msg, MsgSender sender){
        String date=new SimpleDateFormat(dateFormat).format(new Date());
        StringBuilder stringBuilder=new StringBuilder();
        FightStatue fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
        if(fightStatue!=null) {//没有工会boss进度数据
            int no = fightStatue.getLoop() * 10 + fightStatue.getSerial();
            try {
                Knife knife = new Knife(msg.getQQCode(), no, Integer.parseInt(getVar(msg.getMsg())), date);
                int i=DB.Instance.outKnife(knife);
                fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
                switch (i){
                    case -1:
                        break;
                    case 0:
                        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没开始为什么就交刀惹\n");
                        return;
                    case 1:
                        stringBuilder.append("出完一刀记得喝杯水\n");
                        break;
                    case 2:
                        stringBuilder.append("恭喜自救成功\n");
                        break;
                }
                stringBuilder.append("现在boss状态\n周目:").append(fightStatue.getLoop()).append("\n").append(fightStatue.getSerial()).append("王\n");
                if(fightStatue.getRemnant()==-1){
                    stringBuilder.append("血量：未录入");
                }else {
                    stringBuilder.append("血量：").append(fightStatue.getRemnant());
                }
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), stringBuilder.toString());
            } catch (NumberFormatException e) {
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), commandError);
            }
        }else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "还没开始为什么就交刀惹");
        }
    }




}