package cn.sciuridae.CoolQ.timeJob;

//提醒买药小助手

import com.forte.qqrobot.anno.timetask.CronTask;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.timetask.TimeJob;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.util.Set;

import static cn.sciuridae.CoolQ.Listener.prcnessIntercept.On;
import static cn.sciuridae.constant.canSendImage;

@CronTask("0 0 0,6,12,18 * * ?")
public class tixingmaiyao implements TimeJob {
    @Override
    public void execute(MsgSender msgSender, CQCodeUtil cqCodeUtil) {
      try {
          Set<String> strings = On.keySet();
          for (String s : strings) {
              if (On.get(s).isButon()) {
                  if (canSendImage) {
                      msgSender.SENDER.sendGroupMsg(s, "我是每日提醒买药小助手，请和我一起做每天买满4次药的大人吧\n[CQ:image,file=买药.png]");
                  } else {
                      msgSender.SENDER.sendGroupMsg(s, "我是每日提醒买药小助手，请和我一起做每天买满4次药的大人吧");
                  }
              }
          }
      }catch (NullPointerException e){

      }


    }
}
