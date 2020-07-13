package cn.sciuridae.timer;

//提醒买药小助手

import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

import static cn.sciuridae.constant.pricnessConfig;
import static cn.sciuridae.listener.Intercept.prcnessIntercept.On;

@Component
@CronTask("0 0 0,6,12,18 * * ?")
public class tixingmaiyao implements TimeJob {
    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
        try {
            File file = new File("./" + pricnessConfig.getTixingmaiyao());
            String str = null;
            if (file.exists()) {
                CQCode cqCode_image = cqCodeUtil.getCQCode_Image(file.getAbsolutePath());
                str=cqCode_image.toString();
            }
            Set<String> strings = On.keySet();
            for (String s : strings) {
                if (On.get(s).isButon()) {
                    msgSender.SENDER.sendGroupMsg(s, "我是每日提醒买药小助手，请和我一起做每天买满4次药的大人吧\n" + str);
                }
            }
        } catch (NullPointerException e) {
        }
    }
}
