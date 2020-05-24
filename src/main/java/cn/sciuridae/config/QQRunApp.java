package cn.sciuridae.config;

import cn.sciuridae.CoolQ.Listener.prcnessListener;
import cn.sciuridae.DB.sqLite.DB;
import cn.sciuridae.constant;
import com.forte.component.forcoolqhttpapi.CoolQHttpApp;
import com.forte.component.forcoolqhttpapi.CoolQHttpConfiguration;
import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.GroupMemberList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.beans.messages.result.inner.GroupMember;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.BeanFactory;


import java.util.*;

import static cn.sciuridae.CoolQ.Listener.prcnessListener.eggOn;
import static cn.sciuridae.Tools.ApiConnect.getImagePower;
import static cn.sciuridae.constant.clearTree;
import static cn.sciuridae.constant.pcrGroupMap;

@SimpleRobotApplication(resources = "/conf.properties")

public class QQRunApp implements CoolQHttpApp {

    private BeanFactory beanFactory;

    public QQRunApp(BeanFactory beanFactory){
        this.beanFactory = beanFactory;
    }

    @Override
    public void before(CoolQHttpConfiguration configuration) {
        // 整合Spring的DependGetter
        configuration.setDependGetter(new SpringDependGetter(beanFactory));
        DB db = DB.getInstance();
        db.init();
    }

    @Override
    public void after(CQCodeUtil cqCodeUtil, MsgSender sender) {
        constant.robotQQ=sender.GETTER.getLoginQQInfo().getQQ();//获取机器人qq
        Map<String, List<String>> map = DB.Instance.clearTree();//清除过期树信息
        //如果有强制下树的人员
        if(!map.isEmpty()){
            StringBuilder stringBuilder=new StringBuilder();
            Set<String> groupNames =map.keySet();
            for(String groupName:groupNames){
                stringBuilder.append(clearTree);
                for(String userQQ:map.get(groupName)){
                    stringBuilder.append("[CQ:at,qq=").append(userQQ).append("]");
                }
                sender.SENDER.sendGroupMsg(groupName, stringBuilder.toString());
                stringBuilder.delete(0,stringBuilder.length());
            }
        }


        //获取所有群成员昵称和权限
        pcrGroupMap = new HashMap<>();
        eggOn=new HashMap<>();
        prcnessListener.powerList=new HashMap<>();
        GroupList groups = sender.GETTER.getGroupList();
        for (Group group : groups.getList()) {
            GroupMemberList groupMember = sender.GETTER.getGroupMemberList(group.getCode());
            List<String> list=new ArrayList<>();
            eggOn.put(group.getCode(),true);
            for (GroupMember s : groupMember) {
                pcrGroupMap.put(s.getQQ(), s.getName());
                if(s.getPower().isAdmin()||s.getPower().isOwner()){
                    list.add(s.getQQ());
                }
            }
            prcnessListener.powerList.put(group.getCode(),list);
        }

        //获取这个机器人能不能发图片
        constant.canSendImage=getImagePower();
        System.out.println("启动成功");
        System.out.println("启动成功");
        System.out.println("启动成功");
        System.out.println("启动成功");
        System.out.println("启动成功");
    }


}
