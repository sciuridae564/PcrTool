package cn.sciuridae.Listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.util.List;


@Beans
public class testListener {

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = "#测试.*" ,at = true)
    public void testListen1(GroupMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);

        for (String s : strings) {
            System.out.println(s);
        }
    }
}
