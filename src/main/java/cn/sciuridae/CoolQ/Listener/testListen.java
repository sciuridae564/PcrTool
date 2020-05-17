package cn.sciuridae.CoolQ.Listener;

import cn.sciuridae.DB.sqLite.DB;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.sciuridae.constant.*;

@Service
public class testListen {
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"测试"})
    public void testListern(PrivateMsg msg, MsgSender sender) {
        //System.out.println(sender.SETTER.setGroupBan(msg.getGroupCode(), msg.getQQCode(), 1));
//        CQCodeUtil cqCodeUtil = CQCodeUtil.build();
//        List<String> strings = cqCodeUtil.getCQCodeStrFromMsgByType(msg.getMsg(), CQCodeTypes.at);
//        for (String s:strings){
//            System.out.println(s);
//        }
//        List<Integer> list= DB.Instance.getAllGroupQQInFight();
//        File file=new File("test.xls");
//        excelWrite excelWrite=new excelWrite(file);
//        excelWrite.writedDate();
//        excelWrite.reflashFile();
        System.out.println("aaaaaaaaaaaaaaaaaaaa");
        List<Integer> list = DB.Instance.searchDeadLineGroup();
        //清理数据
        Map<String, List<String>> map = DB.Instance.clearTree(list);
        DB.Instance.endFight(list);
        Set<String> GroupQQs = map.keySet();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> badMan;
        for (String GroupQQ : GroupQQs) {
            stringBuilder.delete(0, stringBuilder.length());
            badMan = map.get(GroupQQ);
            stringBuilder.append("会战结束，辛苦辛苦\n");
            if(badMan!=null){
                stringBuilder.append("但是：");
                for (String QQ : badMan) {
                    stringBuilder.append("[CQ:at,qq=").append(QQ).append("] ");
                }
                stringBuilder.append("他们还在出刀。都已经结束啦");
            }
            sender.SENDER.sendPrivateMsg("1728817446", stringBuilder.toString());
        }

    }
}
