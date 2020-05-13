package cn.sciuridae.CoolQ.Listener;

import cn.sciuridae.DB.sqLite.DB;
import cn.sciuridae.Excel.excelWrite;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.CQCodeTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.sciuridae.Tools.stringTool.getExcelFileName;
import static cn.sciuridae.constant.*;

public class testListen {
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"生成excel.*"})
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


    }
}
