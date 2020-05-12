package cn.sciuridae.CoolQ.Listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.util.List;

public class testListen {
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"测试.*"})
    public void testListern(GroupMsg msg, MsgSender sender) {
        //System.out.println(sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1));
//        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
//        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
//        for (String s:strings){
//            System.out.println(s);
//        }
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), msg.getMsg());
    }
}
