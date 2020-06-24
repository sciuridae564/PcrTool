package cn.sciuridae.listener;

import cn.sciuridae.dataBase.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class testListen {
    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    KnifeListService knifeListServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    PcrUnionService pcrUnionServiceImpl;
//
    /*@Listen(MsgGetTypes.groupMsg)
    public void testListern(GroupMsg msg, MsgSender sender) {
        sender.SENDER.sendGroupMsg(msg.getGroupCode(),msg.getMsg());
    }*/
//
//
////
////    @Listen(MsgGetTypes.groupMsg)
////    @Filter(value = {"^sdf$"},keywordMatchType = KeywordMatchType.TRIM_REGEX)
////    public void testListern1(GroupMsg msg, MsgSender sender) {
////        sender.SENDER.sendGroupMsg(msg.getGroupCode(),msg.getMsg());
////    }
////
//    @Listen(MsgGetTypes.privateMsg)
//    @Filter(value = {"azaz"},keywordMatchType = KeywordMatchType.EQUALS)
//    public void testListern11(PrivateMsg msg, MsgSender sender) {
//        List<KnifeList> knife = knifeListServiceImpl.getKnife(522828248L);
//        knife.forEach(System.out::println);
//    }
//
//        ProgressServiceImpl.removeById(progress.getId());
//        sender.SENDER.sendPrivateMsg("1728817446", SuccessEndFight);
//    }
//    @Listen(MsgGetTypes.privateMsg)
//    @Filter("aaa")
//    public void testListern12(PrivateMsg msg, MsgSender sender) {
//        Progress progress = new Progress();
//        progress.setLoop(1);
//        progress.setSerial(1);
//        progress.setRemnant(BossHpLimit[0]);
//        progress.setTeamQQ(680495962L);
//        progress.setVersion(1);
//        progress.setStartTime(LocalDateTime.now());
//        progress.setEndTime(LocalDateTime.now().plusDays(8));
//
//        ProgressServiceImpl.save(progress);
//    }


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
