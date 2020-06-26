package cn.sciuridae.Service;

import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.bean.Tree;
import cn.sciuridae.dataBase.service.ProgressService;
import cn.sciuridae.dataBase.service.ScoresService;
import cn.sciuridae.dataBase.service.TreeService;
import cn.sciuridae.utils.bilibili.BilibiliLive;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.sciuridae.listener.bilibiliListener.liveHashMap;

@Service
public class springRunAfter implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    ScoresService ScoresServiceImpl;

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
                List<Tree> list = treeServiceImpl.deletTreeByGroup(progress);
                stringBuilder.append("自动重置会战次数拉");
                if (list != null) {
                    for (Tree tree : list) {
                        stringBuilder.append(cqCodeUtil.getCQCode_At(tree.getTeamQQ().toString()));
                    }
                    stringBuilder.append("强制下树惹");
                }
                sender.SENDER.sendGroupMsg(progress.toString(), stringBuilder.toString());
            }
        }

        //直播监听
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
