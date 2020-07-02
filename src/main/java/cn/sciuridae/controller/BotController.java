package cn.sciuridae.controller;

import com.forte.qqrobot.MsgParser;
import com.forte.qqrobot.MsgProcessor;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.listener.result.ListenResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Controller
@RequestMapping("/coolq")
public class BotController {

    /**
     * 消息字符串转化器
     */
    @Autowired
    private MsgParser parser;

    /**
     * 监听触发器
     */
    @Autowired
    private MsgProcessor processor;

    /**
     * 接收post请求，此处为接收来自酷Q插件的请求，
     * 酷Q插件中的上报路径为：（假设在同一台电脑上）http://127.0.0.1:8877/coolq/listen
     * 其中的8877为springboot的web配置
     *
     * @param request 原生的httpServletRequest请求参数，需要从其中获取到请求的原始字符串
     * @return
     */
    @PostMapping("/listen")
    @ResponseBody
    public Object listen(HttpServletRequest request) {
        // 通过HttpServletRequest的reader流获取请求字符串
        try (final BufferedReader reader = request.getReader()) {
            String data = reader.lines().collect(Collectors.joining());
            // 尝试解析此字符串，如果解析成功，则说明可以进行消息处理
            final MsgGet msgGet = parser.parse(data);
            if (msgGet != null) {
                // 进行消息监听处理, 此方法会获取到所有的监听响应值
                // ListenResult[] listenResult = processor.onMsg(msgGet);
                // 进行消息监听处理, 并自动选择一个监听响应值对象
                ListenResult listenResult = processor.onMsgSelected(msgGet);
                return listenResult.result();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
