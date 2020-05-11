package cn.sciuridae.CoolQ.Listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

public class testListen {
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "测试")
    public void testListern(GroupMsg msg, MsgSender sender) {
        System.out.println(sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1));
    }
}
