package cn.sciuridae.listener;

import cn.sciuridae.utils.bean.groupPower;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.listener.MsgGetContext;
import com.forte.qqrobot.listener.MsgIntercept;

import java.util.concurrent.ConcurrentHashMap;

@Beans
public class prcnessIntercept implements MsgIntercept {
    public static ConcurrentHashMap<String, groupPower> On = new ConcurrentHashMap<>(10);//1:总开关2:抽卡开关3：提醒买药开关
    //默认总开关开启

    @Override
    public boolean intercept(MsgGetContext context) {
        if (context.getMsgGet() instanceof GroupMsg) {
            try {
                //总体开关
                if (!(On.get(((GroupMsg) context.getMsgGet()).getGroupCode())).isOn()) {
                    if (isOpen(context.getMsgGet().getMsg())) {
                        return true;
                    }
                    return false;
                }

                //抽卡消息过滤
                if (isChouKa(context.getMsgGet().getMsg())) {
                    return On.get(((GroupMsg) context.getMsgGet()).getGroupCode()).isEggon();
                }
            } catch (NullPointerException e) {
                //没这个群的自动都是不通过
                groupPower groupPower = new groupPower();
                On.put(((GroupMsg) context.getMsgGet()).getGroupCode(), groupPower);
                return false;
            }
        }
        return true;
    }


    //是抽卡消息吗
    private boolean isChouKa(String msg) {
        return msg.startsWith("#十连") || msg.startsWith("#up十连") || msg.startsWith("#井") || msg.startsWith("#up井") || msg.startsWith("#抽卡") || msg.startsWith("#up抽卡");
    }

    public boolean isOpen(String msg) {
        if (msg.equals("#开启PcrTool")) {
            return true;
        }
        return false;
    }

}
