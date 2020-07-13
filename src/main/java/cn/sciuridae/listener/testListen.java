package cn.sciuridae.listener;

import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;


@Service
public class testListen {
//    @Autowired
//    TeamMemberService teamMemberServiceImpl;
//    @Autowired
//    KnifeListService knifeListServiceImpl;
//    @Autowired
//    ProgressService ProgressServiceImpl;
//    @Autowired
//    TreeService treeServiceImpl;
//    @Autowired
//    PcrUnionService pcrUnionServiceImpl;

//    @Listen({MsgGetTypes.privateMsg,MsgGetTypes.groupMsg})
//    @Filter("qwe")
//    public void testListern(MsgGet msg, MsgSender sender) {
//        if(msg instanceof PrivateMsg){
//            System.out.println();
//        }else if(msg instanceof GroupMsg){
//            System.out.println();
//        }
//    }
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
//
//        StringBuilder stringBuilder=new StringBuilder();
//
//        try {
//            while (true){
//                stringBuilder.append("好好好好好好好好好好");
//                sender.SENDER.sendPrivateMsg(msg.getQQCode(),stringBuilder.toString());
//            }
//        }catch (Exception e){
//            System.out.println(stringBuilder.toString().length());
//            System.out.println(stringBuilder.toString());
//        }
//    }
//
//
//    @Listen(MsgGetTypes.privateMsg)
//    @Filter(value = {"az"},keywordMatchType = KeywordMatchType.EQUALS)
//    public void testListern1(PrivateMsg msg, MsgSender sender) {
//        List list=knifeListServiceImpl.list();
//        list.forEach(System.out::println);
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
