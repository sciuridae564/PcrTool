package cn.sciuridae.config;

import cn.sciuridae.constant;
import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.service.ScoresService;
import cn.sciuridae.utils.bilibili.BilibiliLive;
import com.forte.component.forcoolqhttpapi.CoolQHttpApp;
import com.forte.component.forcoolqhttpapi.CoolQHttpConfiguration;
import com.forte.qqrobot.SimpleRobotApplication;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.listener.bilibiliListener.liveHashMap;
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
    private BeanFactory beanFactory;

    @Autowired
    ScoresService ScoresServiceImpl;

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
        frashAllLive();//直播

    }

    public void frashAllLive() {
        List<Scores> list = ScoresServiceImpl.getLive();

        for (Scores s : list) {
            if (s.getLive1() != 0 && liveHashMap.get(s.getLive1().toString()) != null) {
                try {
                    liveHashMap.put(String.valueOf(s.getLive1()), new BilibiliLive(String.valueOf(s.getLive1())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (s.getLive2() != 0 && liveHashMap.get(s.getLive2().toString()) != null) {
                try {
                    liveHashMap.put(String.valueOf(s.getLive2()), new BilibiliLive(String.valueOf(s.getLive2())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (s.getLive3() != 0 && liveHashMap.get(s.getLive3().toString()) != null) {
                try {
                    liveHashMap.put(String.valueOf(s.getLive3()), new BilibiliLive(String.valueOf(s.getLive3())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
