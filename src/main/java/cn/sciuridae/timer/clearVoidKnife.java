package cn.sciuridae.timer;

import cn.sciuridae.dataBase.bean.Progress;
import cn.sciuridae.dataBase.bean.Tree;
import cn.sciuridae.dataBase.service.ProgressService;
import cn.sciuridae.dataBase.service.TreeService;
import cn.sciuridae.utils.ExcelWrite;
import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static cn.sciuridae.utils.stringTool.getExcelFileName;


@CronTask("0 0 5 * * ? *") //每天5点，在线刷新
public class clearVoidKnife implements TimeJob {
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;

    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        List<LocalDate> localDates = new ArrayList<>();
        localDates.add(LocalDate.now().plusDays(-1));

        List<Progress> progressList = ProgressServiceImpl.list();
        StringBuilder stringBuilder = new StringBuilder();

        for (Progress progress : progressList) {
            stringBuilder.delete(0, stringBuilder.length());
            List<Tree> list = treeServiceImpl.deletTreeByGroup(progress.getTeamQQ());
            stringBuilder.append("自动重置会战次数拉");
            if (list != null) {
                for (Tree tree : list) {
                    stringBuilder.append(cqCodeUtil.getCQCode_At(tree.getTeamQQ().toString()));
                }
                stringBuilder.append("强制下树惹");
            }
            msgSender.SENDER.sendGroupMsg(progress.getTeamQQ().toString(), stringBuilder.toString());
        }

        //生成每日报表


        for (Progress progress : progressList) {
            try {
                ExcelWrite excelWrite = new ExcelWrite(getExcelFileName(progress.getTeamQQ().toString(), localDates.get(0)),
                        localDates,
                        progress.getTeamQQ());
                excelWrite.writedDate();
                excelWrite.reflashFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
