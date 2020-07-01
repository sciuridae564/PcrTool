package cn.sciuridae.config;

import com.forte.qqrobot.Application;
import com.forte.qqrobot.BaseApplication;
import com.forte.qqrobot.log.QQLog;
import com.simbot.component.mirai.MiraiApplication;
import com.simplerobot.core.springboot.configuration.SpringBootDependGetter;
import com.simplerobot.core.springboot.configuration.SpringbootQQLogBack;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * cq http configuration
 */
@Configuration
public class MiraiSpringConfiguration {

    @Bean
    @ConditionalOnMissingBean(Application.class)
    public MiraiSpringBootBeanFactoryApp getMiraiApp(SpringBootDependGetter dependGetter) {
        return new MiraiSpringBootBeanFactoryApp(dependGetter);
    }

    @Bean
    @ConditionalOnMissingBean(BaseApplication.class)
    public MiraiApplication getMiraiApplication(SpringbootQQLogBack logBack) {
        QQLog.setLogBack(logBack.getLogBack());
        return new MiraiApplication();
    }

}
