package cn.sciuridae.timer;

import cn.sciuridae.dataBase.bean.Progress;
import cn.sciuridae.dataBase.bean.Tree;
import cn.sciuridae.dataBase.service.*;
import cn.sciuridae.utils.ExcelWrite;
import com.forte.qqrobot.bot.BotManager;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.sciuridae.listener.OtherListener.AllCoolDown;
import static cn.sciuridae.utils.stringTool.getExcelFileName;
import static cn.sciuridae.utils.timeUtil.getDescDateList;

@Component
public class clearEnd {
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    ScoresService scoresServiceImpl;
    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    KnifeListService knifeListServiceImpl;
    @Autowired
    BotManager botManager;

    @Scheduled(cron = "0 0 0 * * ? ")
    public void execute() {
        MsgSender msgSender = botManager.defaultBot().getSender();
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        LocalDateTime localDateTime = LocalDateTime.now();
        List<Long> list = ProgressServiceImpl.getEnd(localDateTime);//获取所有应该结束工会战的工会
        StringBuilder stringBuilder = new StringBuilder();

        //清理他们的树
        for (Long GroupQQ : list) {
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append("会战结束，辛苦辛苦\n");
            List<Tree> list1 = treeServiceImpl.deletTreeByGroup(GroupQQ);
            if (list1 != null) {
                for (Tree tree : list1) {
                    stringBuilder.append("[CQ:at,qq=").append(tree.getTeamQQ()).append("] ");
                }
                stringBuilder.append("他们还在出刀。都已经结束啦");
            }
            msgSender.SENDER.sendGroupMsg(GroupQQ.toString(), stringBuilder.toString());
        }

        //生成报表
        for (Long id : list) {
            try {
                Progress progress = ProgressServiceImpl.getProgress(id);
                List<LocalDate> localDates = new ArrayList<>(getDescDateList(progress.getStartTime().toLocalDate(), progress.getEndTime().toLocalDate().plusDays(-1)));
                ExcelWrite excelWrite = new ExcelWrite(getExcelFileName(id.toString(), progress.getStartTime().toLocalDate(), progress.getEndTime().toLocalDate()), localDates, id,
                        teamMemberServiceImpl,
                        knifeListServiceImpl);
                excelWrite.writedDate();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AllCoolDown();

        //签到重置
        scoresServiceImpl.clearSign();

    }
}
