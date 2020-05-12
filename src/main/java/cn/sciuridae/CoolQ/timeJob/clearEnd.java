package cn.sciuridae.CoolQ.timeJob;

import cn.sciuridae.DB.sqLite.DB;
import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.sciuridae.CoolQ.Listener.prcnessListener.AllCoolDown;

@CronTask("0 0 0 1/1 * ? *")
public class clearEnd implements TimeJob {

    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        List<Integer> list = DB.Instance.searchDeadLineGroup();
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
