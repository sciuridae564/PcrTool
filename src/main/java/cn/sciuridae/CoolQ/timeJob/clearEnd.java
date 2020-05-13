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

import static cn.sciuridae.CoolQ.Listener.prcnessListener.AllCoolDown;
import static cn.sciuridae.Tools.stringTool.getExcelFileName;
import static cn.sciuridae.constant.ExcelDir;

@CronTask("0 0 0 1/1 * ? *")
public class clearEnd implements TimeJob {

    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        List<Integer> list = DB.Instance.searchDeadLineGroup();

        //生成报表
        for (Integer id : list) {
            File file = null;
            try {
                List<Date> timelist = DB.Instance.getDateList(id);
                String groupQQ = DB.Instance.searchGroupNameByID(id);//工会qq
                file = new File(getExcelFileName(groupQQ, timelist.get(0), timelist.get(timelist.size() - 1)));
                excelWrite excelWrite = new excelWrite(file, timelist, id);
                excelWrite.writedDate();
                excelWrite.reflashFile();
            } catch (NullPointerException e) {
                if (file != null && file.exists()) {
                    file.delete();
                }
            }

        }

        //清理数据
        Map<String, List<String>> map = DB.Instance.clearTree(list);
        Set<String> GroupQQs = map.keySet();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> badMan;
        for (String GroupQQ : GroupQQs) {
            stringBuilder.delete(0, stringBuilder.length());
            badMan = map.get(GroupQQ);
            stringBuilder.append("会战结束，辛苦辛苦\n但是：");
            for (String QQ : badMan) {
                stringBuilder.append("[CQ:at,qq=").append(QQ).append("] ");
            }
            stringBuilder.append("他们还在出刀。都已经结束啦");
            msgSender.SENDER.sendGroupMsg(GroupQQ, stringBuilder.toString());
        }
        AllCoolDown();
    }
}
