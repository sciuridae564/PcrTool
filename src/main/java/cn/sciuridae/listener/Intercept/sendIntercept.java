package cn.sciuridae.listener.Intercept;

import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.sender.intercept.SendContext;
import com.forte.qqrobot.sender.intercept.SenderSendIntercept;
import com.forte.qqrobot.sender.senderlist.SenderSendList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


//临时解决一下mirai发送数据超过870字节会抛异常的错误
@Beans
public class sendIntercept implements SenderSendIntercept {
    @Override
    public boolean intercept(SendContext context) {
        SenderSendList value = context.getValue();
        Method method = context.getMethod();
        Object[] params =  context.getParams();
        String name = method.getName();


        if(name.equals("sendPrivateMsg")){
            List<String> list=getStrList((String) params[1],300);
            for(String s:list){
                value.sendPrivateMsg( (String)params[0],s);
            }
            return false;
        }

        if(name.equals("sendGroupMsg")){
            List<String> list=getStrList((String)params[1],300);
            for(String s:list){
                value.sendGroupMsg((String)params[0],s);
            }

            return false;
        }

        return true;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString
     *            原始字符串
     * @param length
     *            指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length
                                          ) {
        List<String> list = new ArrayList<>();
        int index=0;
        while (index * length < inputString.length()){
            String childStr = substring(inputString, index * length,
                    ( index+1 )  * length);
            list.add(childStr);
            index++;
        }
        return list;
    }
    /**
     * 分割字符串，如果开始位置大于字符串长度，返回空
     *
     * @param str
     *            原始字符串
     * @param f
     *            开始位置
     * @param t
     *            结束位置
     * @return
     */
    public static String substring(String str, int f, int t) {
        if (f > str.length())
            return null;
        if (t > str.length()) {
            return str.substring(f);
        } else {
            return str.substring(f, t);
        }
    }
}
