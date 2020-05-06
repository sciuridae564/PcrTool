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
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import java.text.SimpleDateFormat;
import java.util.Date;

import static cn.sciuridae.Tools.stringTool.*;
import static cn.sciuridae.constant.*;

@Beans
public class prcnessListener {

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#帮助.*" ,at = true)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), helpMsg);
    }

    /**
     * 格式 #建会@机器人 工会名 自己的游戏名字（没有@工会长默认创建者为工会长）
     * @param msg
     * @param sender
     */
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#建会.*" ,at = true)
    public void createGroup(GroupMsg msg, MsgSender sender){
        robotQQ= msg.getThisCode();
        String str=msg.getMsg();
        int atNum=searchAtNumber(str);
        String groupMasterQQ=null;
        String groupName=null;
        String gameName=null;
        String[] strings=msg.getMsg().split(" +");

        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        Group group=new Group();
        group.setCreateDate(df.format(date));
        group.setGroupid(msg.getGroupCode());

        //暂时不支持替别人建会
        if(strings.length>4){
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
            return;
        }
        try {
            gameName=strings[3];//会长名字
            if(atNum>2){
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
                return;
            }else if(atNum==1){//只有at了机器人
                groupMasterQQ=msg.getQQ();
                groupName=strings[2];//工会名字
                group.setGroupName(groupName);
                group.setGroupMasterQQ(groupMasterQQ);
                DB.Instance.creatGroup(group,gameName);

            }
//            else {//有at其他人
//                groupMasterQQ=strings[4].substring(10,strings[4].indexOf("]"));
//                groupName=strings[2];//工会名字
//                group.setGroupName(groupName);
//                group.setGroupMasterQQ(groupMasterQQ);
//                gameName=null;
//                teamMember teamMember=new teamMember(msg.getQQCode(),false,null,strings[3]);
//                System.out.println(group); System.out.println(gameName);System.out.println(teamMember);
//                //DB.Instance.creatGroup(group,gameName,teamMember);
//            }
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), tips_error);
        }


    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#入会.*" ,at = true)
    public void getGroup(GroupMsg msg, MsgSender sender){
        teamMember teamMember=new teamMember(msg.getQQCode(),false,null, getVar(msg.getMsg()));
        System.out.println("teammember"+teamMember);
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
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), successJoinGroup);//成功加入
                break;
            case 3:
                sender.SENDER.sendGroupMsg(msg.getGroupCode(), noThisGroup);//成功加入
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

                 DB.Instance.hurtfight(msg.getQQCode(),getHurt(msg.getMsg()),sender);
                fightStatue=DB.Instance.searchFightStatue(msg.getQQCode());
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