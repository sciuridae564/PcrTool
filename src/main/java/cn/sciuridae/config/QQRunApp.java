package cn.sciuridae.config;

import cn.sciuridae.constant;
import cn.sciuridae.dataBase.service.TreeService;
import com.forte.component.forcoolqhttpapi.CoolQHttpApp;
import com.forte.component.forcoolqhttpapi.CoolQHttpConfiguration;
import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.utils.ApiConnect.getImagePower;
import static cn.sciuridae.utils.ApiConnect.getLocalIp4AddressFromNetworkInterface;

/**
 * RunApp实现了{@link CoolQHttpApp}接口，因此依旧会执行before和after
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
// resources代表读取/conf.properties配置文件
@SimpleRobotApplication(resources = "/conf.properties")
public class QQRunApp implements CoolQHttpApp {
    @Autowired
    TreeService TreeServiceImpl;

    private BeanFactory beanFactory;

    public QQRunApp(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void before(CoolQHttpConfiguration configuration) {
        // 整合Spring的DependGetter
        configuration.setDependGetter(new SpringDependGetter(beanFactory));

    }

    @Override
    public void after(CQCodeUtil cqCodeUtil, MsgSender sender) {
        constant.robotQQ = sender.GETTER.getLoginQQInfo().getQQ();//获取机器人qq

//        //清除过期树信息
//        Map<String, List<String>> map = DB.Instance.clearTree();
//        //如果有强制下树的人员
//        if(!map.isEmpty()){
//            StringBuilder stringBuilder=new StringBuilder();
//            Set<String> groupNames =map.keySet();
//            for(String groupName:groupNames){
//                stringBuilder.append(clearTree);
//                for(String userQQ:map.get(groupName)){
//                    stringBuilder.append("[CQ:at,qq=").append(userQQ).append("]");
//                }
//                sender.SENDER.sendGroupMsg(groupName, stringBuilder.toString());
//                stringBuilder.delete(0,stringBuilder.length());
//            }
//        }

        //获取这个机器人能不能发图片
        constant.canSendImage = getImagePower();
        //获取本机ip地址
        constant.ip = getLocalIp4AddressFromNetworkInterface();
        //读取配置文件
        getFile();
        getconfig();
        getEvent();
    }
}
