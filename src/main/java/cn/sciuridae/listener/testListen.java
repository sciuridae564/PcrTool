package cn.sciuridae.listener;

import cn.sciuridae.dataBase.service.PcrUnionService;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.sciuridae.constant.pricnessConfig;

@Service
public class testListen {

//
//    @Autowired
//    ScoresService ScoresServiceImpl;

    @Autowired
    PcrUnionService pcrUnionServiceImpl;
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"^q[1-5]ss.*$"},keywordMatchType = KeywordMatchType.RE_CQCODE_EQUALS,at = true)
//    public void testListern(GroupMsg msg, MsgSender sender) {
//        sender.SENDER.sendGroupMsg(msg.getGroupCode(),msg.getMsg());
//    }


//
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"^sdf$"},keywordMatchType = KeywordMatchType.TRIM_REGEX)
//    public void testListern1(GroupMsg msg, MsgSender sender) {
//        sender.SENDER.sendGroupMsg(msg.getGroupCode(),msg.getMsg());
//    }
//
@Listen(MsgGetTypes.privateMsg)
public void testListern11(PrivateMsg msg, MsgSender sender) {
    System.out.println(pricnessConfig);
}


//    @Autowired
//    TeamMemberService teamMemberServiceImpl;
//
//    @Listen(MsgGetTypes.groupMsg)
//    public void testListern(GroupMsg msg, MsgSender sender) {
//        boolean admin = teamMemberServiceImpl.isAdmin(msg.getQQCodeNumber(), msg.getGroupCodeNumber());
//        System.out.println(admin);
//
//        //sender.SENDER.sendPrivateMsg(msg.getQQCode(),byId.toString());
//    }
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"给爷整个活"})
//    public void testListern1(GroupMsg msg, MsgSender sender) {
//       if(msg.getQQCode().equals("1728817446")){
//           sender.SENDER.sendGroupMsg(msg,"[CQ:at,qq=all]");
//       }
//    }


}
