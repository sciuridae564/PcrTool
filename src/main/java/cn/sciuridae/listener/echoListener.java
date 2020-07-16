package cn.sciuridae.listener;

import cn.sciuridae.dataBase.bean.echoMsg;
import cn.sciuridae.dataBase.service.echoMsgService;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import org.omg.CORBA.LongHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

@Service
public class echoListener {

    @Autowired
    echoMsgService echoMsgServiceImpl;
    private final static Boolean _echo=true;

    @Listen(MsgGetTypes.groupMsg)
    public void echo(GroupMsg msg, MsgSender sender) {
        String s=msg.getMsg();
        LocalDateTime localDateTime=LocalDateTime.now();
        String group=msg.getGroup();
        synchronized (_echo){
            List<echoMsg> search = echoMsgServiceImpl.search(s,msg.getGroupCodeNumber());

            if(search.size()>0){
                //不接受自己复读自己
                for(echoMsg echoMsg:search){
                    if(msg.getQQCodeNumber().equals(echoMsg.getQq_number())&&msg.getGroupCodeNumber().equals(echoMsg.getGroup_number())){
                        return;
                    }
                }

                //先判断下这玩意是不是被自己复读过了
                if(!search.get(0).isEchoOk()){
                    //没有被复读过
                    //看看这句话后面有没有要复读的
                    echoMsg root = search.get(0);
                    List<echoMsg> echoMsgs = echoMsgServiceImpl.af_search(root.getGroup_number(), root.getQq_number(), root.getTime());//第一个人此前说过的话
                    if(echoMsgs.size()==0){
                        //复读最后一个，看看前面需不需要复读，找到最大的复读序列
                        LinkedList<String> list = searchEcho(s, msg.getGroupCodeNumber(), msg.getQQCodeNumber());
                        while (list.size()>0){
                            sender.SENDER.sendGroupMsg(group,list.pollLast());
                        }
                    }else {
                        //复读的不是最后一个
                        if(search.size()>1){
                            //如果此前已经有两个人复读了，那么有可能是需要复读的话后面有一句不需要复读的话
                            //如果这个复读序列只有一句话，则可以复读，否则不复读只记录
                            List<echoMsg> echoMsgs1 = echoMsgServiceImpl.af_search(msg.getGroupCodeNumber(), search.get(1).getQq_number(), LocalDateTime.now());//数据库里的另一位复读者
                            //先判定第二个复读的人没有复读第一个人后面的话
                            int i=0;
                            for (;i<echoMsgs1.size();i++){
                                if(!echoMsgs.get(i).getMsg().equals(echoMsgs1.get(i).getMsg())){
                                    break;
                                }
                            }
                            if(i==0){
                                //可能复读终止在这句话而不是在按时间的最后一句
                                LinkedList<String> list = searchEcho(s, msg.getGroupCodeNumber(), msg.getQQCodeNumber());
                                while (list.size()>0){
                                    sender.SENDER.sendGroupMsg(group,list.pollLast());
                                }

                            }else {
                                //复读了，说明这个人没有复读完成
                                save(msg.getGroupCodeNumber(),msg.getQQCodeNumber(),s,localDateTime);
                            }
                        }else {
                            //第二个人可能还没有复读完成，不是复读的时机，先存下来
                            save(msg.getGroupCodeNumber(),msg.getQQCodeNumber(),s,localDateTime);
                        }
                    }
                }
            }else {
                //数据库里没有存在同群里有 相同的话出现，记录下来
                save(msg.getGroupCodeNumber(),msg.getQQCodeNumber(),s,localDateTime);
            }
        }

    }

    //寻找这个群以end为复读结尾的复读序列，并置这些复读语句为已复读,已最先说复读话的人作为复读起源，传入的为复读参照
    private LinkedList<String> searchEcho(String end, long group,long qq){
        List<echoMsg> search = echoMsgServiceImpl.search(end, group);
        List<echoMsg> echoMsgs = echoMsgServiceImpl.be_search(group, search.get(0).getQq_number(), search.get(0).getTime());//第一个人说话序列
        List<echoMsg> echoMsgs1 = echoMsgServiceImpl.be_search(group, qq, search.get(0).getTime());//参照说话序列
        int i=0;
        for(;i<echoMsgs1.size()&&i<echoMsgs.size();i++){
            if(!echoMsgs.get(i).getMsg().equals(echoMsgs1.get(i).getMsg())){
                break;
            }
        }

        //删除这些复读
        echoMsgServiceImpl.delect(end,group);
        for(int j=0;j<i;j++){
            echoMsgServiceImpl.delect(echoMsgs.get(j).getMsg(),group);
        }
        //置复读完成标志
        //填充复读语句
        LinkedList<String> strings=new LinkedList<>();
        strings.add(end);
        search.get(0).setEchoOk(true);
        echoMsgServiceImpl.save(search.get(0));
        for(int j=0;j<i;j++){
            echoMsgs.get(j).setEchoOk(true);
            echoMsgServiceImpl.save(echoMsgs.get(j));
            strings.add(echoMsgs.get(j).getMsg());
        }
        return strings;
    }

    private void save(long group,long qq,String msg,LocalDateTime localDateTime){
        echoMsg echoMsg=new echoMsg();
        echoMsg.setGroup_number(group);
        echoMsg.setQq_number(qq);
        echoMsg.setMsg(msg);
        echoMsg.setTime(localDateTime);
        echoMsgServiceImpl.save(echoMsg);
    }

}
