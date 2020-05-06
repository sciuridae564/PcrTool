package cn.sciuridae;

import cn.sciuridae.sqLite.DB;
import com.forte.component.forcoolqhttpapi.CoolQHttpApp;
import com.forte.component.forcoolqhttpapi.CoolQHttpApplication;
import com.forte.component.forcoolqhttpapi.CoolQHttpConfiguration;
import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static cn.sciuridae.constant.clearTree;

@SimpleRobotApplication(resources = "/conf.properties")
public class RunApplication implements CoolQHttpApp {
    public static void main(String[] arpg){
        CoolQHttpApplication application = new CoolQHttpApplication();
        application.run(new RunApplication());
    }

    public void before(CoolQHttpConfiguration configuration) {
        configuration.setJavaPort(15514);
        configuration.setLocalServerPort(5700);
        configuration.setServerPath("/coolq");
        DB db =DB.getInstance();
        db.init();

    }

    public void after(CQCodeUtil cqCodeUtil, MsgSender sender) {
        constant.robotQQ=sender.GETTER.getLoginQQInfo().getQQ();//获取机器人qq
        Map<String, LinkedList<String>> map=DB.Instance.clearTree();
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

    }
}
