package cn.sciuridae.CoolQ.timeJob;

import cn.sciuridae.DB.sqLite.DB;
import cn.sciuridae.Excel.excelWrite;
import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.sciuridae.Tools.stringTool.getExcelFileName;
import static cn.sciuridae.constant.ExcelDir;
import static cn.sciuridae.constant.dfForFile;

@CronTask("0 0 5 * * ? *") //每天5点，在线刷新
public class clearVoidKnife implements TimeJob {

    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        Map<String, List<String>> map = DB.Instance.clearTree(DB.Instance.searchAllGroupOnProgress());
        Set<String> GroupQQs = map.keySet();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> badMan;
        for (String GroupQQ : GroupQQs) {
            stringBuilder.delete(0, stringBuilder.length());
            badMan = map.get(GroupQQ);
            stringBuilder.append("自动重置会战次数拉：");
            for (String QQ : badMan) {
                stringBuilder.append("[CQ:at,qq=").append(QQ).append("] ");
            }
            msgSender.SENDER.sendGroupMsg(GroupQQ, stringBuilder.toString());
        }

        //生成每日报表

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -1);
        ArrayList<Date> list = new ArrayList<>();
        list.add(c.getTime());
        //所有在进行工会战的id
        List<Integer> groupIds = DB.Instance.searchAllGroupOnProgress();
        for (Integer id : groupIds) {
            File file = null;
            try {
                String groupQQ = DB.Instance.searchGroupNameByID(id);//工会qq
                file = new File(getExcelFileName(groupQQ, date));
                excelWrite excelWrite = new excelWrite(file, list, id);
                excelWrite.writedDate();
                excelWrite.reflashFile();
            } catch (NullPointerException e) {
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }
    }
}
