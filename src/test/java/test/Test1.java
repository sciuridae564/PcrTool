package test;

import com.alibaba.fastjson.JSONObject;
import com.forte.component.forcoolqhttpapi.beans.msg.QQGroupMsg;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Test1 {
    public static void main(String[] args) {
        String st = "data: {\"anonymous\":null,\"font\":7404624,\"group_id\":1079721245,\"message\":\"11\",\"message_id\":731,\"message_type\":\"group\",\"post_type\":\"message\",\"raw_message\":\"11\",\"self_id\":3521361891,\"sender\":{\"age\":11,\"area\":\"济南\",\"card\":\"\",\"level\":\"潜水\",\"nickname\":\"法欧特斯卡雷特\",\"role\":\"owner\",\"sex\":\"female\",\"title\":\"\",\"user_id\":1149159218},\"sub_type\":\"normal\",\"time\":1583949867,\"user_id\":1149159218}\n";
        System.out.println(st);
        st = st.substring(st.indexOf("data:") + 5);
        System.out.println(st);

        final JSONObject jsonObject = JSONObject.parseObject(st);

        System.out.println(jsonObject);

        final QQGroupMsg qqGroupMsg = jsonObject.toJavaObject(QQGroupMsg.class);
        System.out.println(qqGroupMsg);


    }
}
