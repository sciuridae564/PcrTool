package cn.sciuridae.config;

import com.forte.component.forcoolqhttpapi.CoolQHttpApp;
import com.forte.component.forcoolqhttpapi.CoolQHttpConfiguration;
import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.BeanFactory;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.utils.ApiConnect.getImagePower;
import static cn.sciuridae.utils.ApiConnect.getLocalIp4AddressFromNetworkInterface;

/**
 * RunApp实现了{@link CoolQHttpApp}接口，因此依旧会执行before和after
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
// resources代表读取/conf.properties配置文件
@SimpleRobotApplication(resources = "./conf.properties")
public class QQRunApp implements CoolQHttpApp {
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


        //获取这个机器人能不能发图片
        constant.canSendImage = getImagePower();
        //获取本机ip地址
        constant.ip = getLocalIp4AddressFromNetworkInterface();
        //读取配置文件
        getFile();//群组配置文件
        getconfig();//通用设置
        getEvent();//马事件
        getgachi();//扭蛋

    }



}
