package cn.sciuridae.CoolQ.timeJob;

import cn.sciuridae.DB.sqLite.DB;
import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

@CronTask("0 0 5 * * ? *")
public class clearVoidKnife implements TimeJob {

    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        Map<String, List<String>> map = DB.Instance.clearTree();
        Set<String> GroupQQs = map.keySet();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> badMan;
        for (String GroupQQ : GroupQQs) {
            stringBuilder.delete(0, stringBuilder.length());
            badMan = map.get(GroupQQ);
            stringBuilder.append("自动重置会战次数拉：");
            for (String QQ : badMan) {
                stringBuilder.append("[CQ:at.qq=").append(QQ).append("] ");
            }
            msgSender.SENDER.sendGroupMsg(GroupQQ, stringBuilder.toString());
        }

    }
}
