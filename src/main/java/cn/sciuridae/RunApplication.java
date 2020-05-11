package cn.sciuridae;

import cn.sciuridae.DB.sqLite.DB;
import com.forte.component.forcoolqhttpapi.CoolQHttpApp;
import com.forte.component.forcoolqhttpapi.CoolQHttpApplication;
import com.forte.component.forcoolqhttpapi.CoolQHttpConfiguration;
import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.beans.messages.result.GroupList;
import com.forte.qqrobot.beans.messages.result.GroupMemberList;
import com.forte.qqrobot.beans.messages.result.inner.Group;
import com.forte.qqrobot.beans.messages.result.inner.GroupMember;
import com.forte.qqrobot.exception.BotVerifyException;
import com.forte.qqrobot.log.LogLevel;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Component;

import java.util.*;

import static cn.sciuridae.constant.clearTree;
import static cn.sciuridae.constant.pcrGroupMap;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SimpleRobotApplication(resources = "/conf.properties")
public class RunApplication implements CoolQHttpApp {
    public static void main(String[] arpg){

        //SpringApplication.run(RunApplication.class,arpg);

        CoolQHttpApplication application = new CoolQHttpApplication();
        try {
            application.run(RunApplication.class);
        } catch (BotVerifyException e) {
            System.out.println("\n\n没开cqhttp插件吗？，或者是没有配置cqhttp？启动失败惹");
            Scanner scanner = new Scanner(System.in);
            scanner.next();
        }

    }

    public void before(CoolQHttpConfiguration configuration) {
        DB db =DB.getInstance();
        db.init();
    }

    public void after(CQCodeUtil cqCodeUtil, MsgSender sender) {
        constant.robotQQ=sender.GETTER.getLoginQQInfo().getQQ();//获取机器人qq
        Map<String, List<String>> map = DB.Instance.clearTree();
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


        //获取所有群成员
        pcrGroupMap = new HashMap<>();
        GroupList groups = sender.GETTER.getGroupList();
        for (Group group : groups.getList()) {
            GroupMemberList groupMember = sender.GETTER.getGroupMemberList(group.getCode());
            for (GroupMember s : groupMember) {
                pcrGroupMap.put(s.getQQ(), s.getName());
            }
        }

        System.out.println("启动成功");
        System.out.println("启动成功");
        System.out.println("启动成功");
        System.out.println("启动成功");
        System.out.println("启动成功");


    }

    //开启springboot后自动启动提示界面
    @Component
    public class RunHomePage implements CommandLineRunner {
        public void run(String... args) throws Exception {
            try {
                Runtime.getRuntime().exec("cmd /c start http://localhost:8080/test.html");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
