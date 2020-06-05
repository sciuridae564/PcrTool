package cn.sciuridae.Service;

import cn.sciuridae.dataBase.service.ProgressService;
import cn.sciuridae.dataBase.service.TreeService;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class springRunAfter implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    /**
     * bot管理器
     */
    @Autowired
    private BotManager botManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            CQCodeUtil cqCodeUtil = CQCodeUtil.build();
            final BotSender sender = botManager.defaultBot().getSender();

            List<LocalDate> localDates = new ArrayList<>();
            localDates.add(LocalDate.now().plusDays(-1));

            List<Long> progressList = ProgressServiceImpl.getEnd(LocalDateTime.now());
            StringBuilder stringBuilder = new StringBuilder();

            for (Long progress : progressList) {
                stringBuilder.delete(0, stringBuilder.length());
                List<String> list = treeServiceImpl.deletTreeByGroup(progress);
                stringBuilder.append("自动重置会战次数拉");
                if (list != null) {
                    for (String qq : list) {
                        stringBuilder.append(cqCodeUtil.getCQCode_At(qq));
                    }
                    stringBuilder.append("强制下树惹");
                }
                sender.SENDER.sendGroupMsg(progress.toString(), stringBuilder.toString());
            }
        }

    }
}
