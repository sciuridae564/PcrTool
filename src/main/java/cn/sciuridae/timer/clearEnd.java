package cn.sciuridae.timer;

import cn.sciuridae.dataBase.bean.Progress;
import cn.sciuridae.dataBase.service.ProgressService;
import cn.sciuridae.dataBase.service.ScoresService;
import cn.sciuridae.dataBase.service.TreeService;
import cn.sciuridae.utils.ExcelWrite;
import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.sciuridae.listener.OtherListener.AllCoolDown;
import static cn.sciuridae.utils.stringTool.getExcelFileName;
import static cn.sciuridae.utils.timeUtil.getDescDateList;


@CronTask("0 0 0 1/1 * ? *")
public class clearEnd implements TimeJob {
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    ScoresService scoresServiceImpl;

    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<Long> list = ProgressServiceImpl.getEnd(localDateTime);//获取所有应该结束工会战的工会
        StringBuilder stringBuilder = new StringBuilder();

        //清理他们的树
        for (Long GroupQQ : list) {
            stringBuilder.delete(0, stringBuilder.length());
            stringBuilder.append("会战结束，辛苦辛苦\n");
            List<String> list1 = treeServiceImpl.deletTreeByGroup(GroupQQ);
            if (list1 != null) {
                for (String QQ : list1) {
                    stringBuilder.append("[CQ:at,qq=").append(QQ).append("] ");
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
                ExcelWrite excelWrite = new ExcelWrite(getExcelFileName(id.toString(), progress.getStartTime().toLocalDate(), progress.getEndTime().toLocalDate()), localDates, id);
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
