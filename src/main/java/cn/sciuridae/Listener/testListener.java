package cn.sciuridae.Listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;


@Beans
public class testListener {

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#测试.*" ,at = true)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        System.out.println("完整消息  "+msg.toString());
        System.out.println("消息  "+msg.getMsg());




    }
}
