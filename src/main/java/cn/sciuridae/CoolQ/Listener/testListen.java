package cn.sciuridae.CoolQ.Listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.springframework.stereotype.Service;

import static cn.sciuridae.Tools.stringTool.spiltByte;

@Service
public class testListen {
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"测试.*"})
    public void testListern(GroupMsg msg, MsgSender sender) {
       // sender.SENDER.sendGroupMsg(msg.getGroupCode())

    }

    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"测试.*"})
    public void testListern(PrivateMsg msg, MsgSender sender) {
        CQCodeUtil cqCodeUtil=CQCodeUtil.build();
        System.out.println(cqCodeUtil.getCQCode_Image("怜.png").toString());
        sender.SENDER.sendPrivateMsg(msg.getQQCode(),cqCodeUtil.getCQCode_Image("怜.png").toString());

    }
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"给爷整个活"})
    public void testListern1(GroupMsg msg, MsgSender sender) {
       if(msg.getQQCode().equals("1728817446")){
           sender.SENDER.sendGroupMsg(msg,"[CQ:at,qq=all]");
       }
    }



}
