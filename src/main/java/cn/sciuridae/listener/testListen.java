package cn.sciuridae.listener;

import org.springframework.stereotype.Service;

@Service
public class testListen {

//
//    @Autowired
//    ScoresService ScoresServiceImpl;

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
//    @Listen(MsgGetTypes.groupMsg)
//    @Filter(value = {"gf"},keywordMatchType = KeywordMatchType.STARTS_WITH)
//    public void testListern11(GroupMsg msg, MsgSender sender) {
//        sender.SENDER.sendGroupMsg(msg.getGroupCode(),msg.getMsg());
//    }


//
//    @Listen(MsgGetTypes.privateMsg)
//    public void testListern(PrivateMsg msg, MsgSender sender) {
//
//        Scores byId = ScoresServiceImpl.getById(msg.getQQCodeNumber());
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
