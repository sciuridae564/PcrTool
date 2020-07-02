package cn.sciuridae.config;

import com.forte.component.forcoolqhttpapi.CQHttpContext;
import com.forte.component.forcoolqhttpapi.CoolQHttpApplication;
import com.forte.qqrobot.MsgParser;
import com.forte.qqrobot.MsgProcessor;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.depend.DependCenter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * SpringBoot的@Configuration类，此类用来启动bot服务并向Springboot中注入一些所需要的对象
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */

@Configuration
public class QQConfig {

    /**
     * 主要用来获取main方法中的args参数
     */
    @Autowired
    private ApplicationArguments arguments;

    /**
     * SpringBoot的bean工厂
     */
    @Autowired
    private BeanFactory beanFactory;

    /**
     * 启动SimpleRobot主程序, 并向Springboot注入{@link CQHttpContext}, 即CQHttp组件的启动结果
     */
    @Bean
    public CQHttpContext startSimpleRobot() {
        CoolQHttpApplication application = new CoolQHttpApplication();
        // 启动...
        // 核心1.9.x新增的启动方法：runWithApplication，用来执行一个启动器实例的同时，使用注解配置。
        return application.runWithApplication(new QQRunApp(beanFactory), arguments.getSourceArgs());
    }

    /**
     * 向Springboot注入字符串消息转化器
     */
    @Bean
    public MsgParser getMsgParser(CQHttpContext context) {
        return context.getMsgParser();
    }

    /**
     * 向Springboot注入监听消息触发器
     */
    @Bean
    public MsgProcessor getMsgProcessor(CQHttpContext context) {
        return context.getMsgProcessor();
    }

    /**
     * 向Springboot注入bot管理器
     */
    @Bean
    public BotManager getBotManager(CQHttpContext context) {
        return context.getBotManager();
    }

    /**
     * 向Springboot注入simpleRobot的依赖中心
     */
    @Bean
    public DependCenter getDependCenter(CQHttpContext context) {
        return context.getDependCenter();
    }

}
