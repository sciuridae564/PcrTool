package cn.sciuridae.timer;

import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.service.ScoresService;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.bot.BotSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import com.simplerobot.modules.utils.KQCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static cn.sciuridae.listener.bilibiliListener.liveHashMap;

@Component
@EnableScheduling//可以在启动类上注解也可以在当前文件
public class BilibiliLive {
    @Autowired
    ScoresService ScoresServiceImpl;
    @Autowired
    BotManager botManager;

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void execute() {
        Set<String> strings = liveHashMap.keySet();
        HashMap<String, cn.sciuridae.utils.bilibili.BilibiliLive> live = new HashMap<>();
        cn.sciuridae.utils.bilibili.BilibiliLive cache;
        int i;
        for (String s : strings) {
            cache = liveHashMap.get(s);
            i = cache.getLiveStatus();
            try {
                cache.frash();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //刷新前没开播刷新后开播了
            if (i == 0 && cache.getLiveStatus() == 1) {
                live.put(s, cache);
            }
        }
        BotSender msgSender = botManager.defaultBot().getSender();

        List<Scores> livepeople = ScoresServiceImpl.getLive();
        StringBuilder stringBuilder = new StringBuilder();
        cn.sciuridae.utils.bilibili.BilibiliLive live1, live2, live3;
        for (Scores people : livepeople) {
            stringBuilder.delete(0, stringBuilder.length());
            if (people.getLive1() != 0) {
                live1 = live.get(people.getLive1().toString());
                if (live1 != null)
                    stringBuilder.append("主播开播辣\n标题").append(live1.getTitle()).append("\n链接").append(live1.getUrl()).append("\n").append(KQCodeUtils.getInstance().toCq("image", "file"+"="+ live1.getCover().getAbsolutePath())).append("\n");
            }
            if (people.getLive2() != 0) {
                live2 = live.get(people.getLive2().toString());
                if (live2 != null)
                    stringBuilder.append(live2.getTitle()).append("\n").append(live2.getUrl()).append(KQCodeUtils.getInstance().toCq("image", "file"+"="+ live2.getCover().getAbsolutePath())).append("\n");
            }
            if (people.getLive3() != 0) {
                live3 = live.get(people.getLive3().toString());
                if (live3 != null)
                    stringBuilder.append(live3.getTitle()).append("\n").append(live3.getUrl()).append(KQCodeUtils.getInstance().toCq("image", "file"+"="+ live3.getCover().getAbsolutePath()));
            }
            if (stringBuilder.length() > 0) {
                msgSender.SENDER.sendPrivateMsg(String.valueOf(people.getQQ()), stringBuilder.toString());
            }
        }
    }
}
