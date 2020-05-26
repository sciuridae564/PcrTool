package cn.sciuridae.CoolQ.Listener;

import cn.sciuridae.CoolQ.PricnessConfig;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.stereotype.Service;

import static cn.sciuridae.CoolQ.Listener.prcnessIntercept.On;
import static cn.sciuridae.Tools.stringTool.spiltByte;
import static cn.sciuridae.constant.pricnessConfig;

@Service
public class testListen {
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"测试.*"})
//    public void testListern(GroupMsg msg, MsgSender sender) {
//       sender.SETTER.setGroupBan(msg.getGroupCode(),msg.getQQCode(),1);
//
//    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"测试.*"})
    public void testListern(PrivateMsg msg, MsgSender sender) {
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), On.toString());

    }
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"给爷整个活"})
//    public void testListern1(GroupMsg msg, MsgSender sender) {
//       if(msg.getQQCode().equals("1728817446")){
//           sender.SENDER.sendGroupMsg(msg,"[CQ:at,qq=all]");
//       }
//    }



}
