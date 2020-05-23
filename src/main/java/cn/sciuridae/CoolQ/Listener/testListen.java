package cn.sciuridae.CoolQ.Listener;

import cn.sciuridae.DB.sqLite.DB;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.sciuridae.Tools.stringTool.getEncoding;
import static cn.sciuridae.Tools.stringTool.spiltByte;
import static cn.sciuridae.constant.*;

@Service
public class testListen {
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"测试.*"})
    public void testListern(GroupMsg msg, MsgSender sender) {
        String needTran = msg.getMsg().replaceAll(" +", "");
//        try {
//            needTran=new String(getUTF8BytesFromGBKString(needTran), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        if (needTran.length() > 2) {
//            needTran = needTran.substring(2);
//            byte[] bytes = needTran.getBytes();
//            StringBuilder tranled = new StringBuilder();
//            StringBuilder tranled1 = new StringBuilder();
//
//            for (int i = 0; i < bytes.length; i++) {
//                tranled1.append("|"+bytes[i]);
//                int[] cache = spiltByte(bytes[i] < 0 ? -bytes[i] + 127 : bytes[i]);
//                tranled1.append("+"+cache[0]+"-"+cache[1]);
//                tranled.append(QieLU[cache[0]]);
//                tranled.append(QieLU[cache[1]]);
//                tranled1.append("+"+QieLU[cache[0]]+"-"+QieLU[cache[1]]);
//            }
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), tranled.toString()+tranled1.toString());
//            sender.SENDER.sendGroupMsg(msg.getGroupCode(), msg.getMsg());
//        } else {
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), getEncoding(needTran));
//        }
    }

    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"给爷整个活"})
    public void testListern1(GroupMsg msg, MsgSender sender) {
       if(msg.getQQCode().equals("1728817446")){
           sender.SENDER.sendGroupMsg(msg,"[CQ:at,qq=all]");
       }
    }

    public static byte[] getUTF8BytesFromGBKString(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }


}
