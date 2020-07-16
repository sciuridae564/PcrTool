package cn.sciuridae.timer;

import cn.sciuridae.dataBase.service.echoMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//定时清除数据库里的复读记录，30分钟因为miria图片缓存时间为30分钟
@Component
public class clearEcho {

    @Autowired
    echoMsgService echoMsgServiceImpl;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void execute() {
        echoMsgServiceImpl.clear();
    }
}
