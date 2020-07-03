package cn.sciuridae.listener.bot_switch;

import cn.sciuridae.aop.Enum.PermEnum;
import cn.sciuridae.aop.anno.Check;
import cn.sciuridae.dataBase.bean.qqGroup;
import cn.sciuridae.dataBase.service.ScoresService;
import cn.sciuridae.dataBase.service.qqGroupService;
import cn.sciuridae.utils.bean.groupPower;
import com.forte.qqrobot.anno.Filter;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.beans.types.KeywordMatchType;
import com.forte.qqrobot.sender.MsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.sciuridae.constant.*;
import static cn.sciuridae.listener.prcnessIntercept.On;

@Service
public class bot_switch {
    @Autowired
    ScoresService ScoresServiceImpl;
    @Autowired
    qqGroupService qqGroupServiceImpl;

    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启扭蛋"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shutEgg(GroupMsg msg, MsgSender sender) {
        try {
            On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setEggon(true));
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
            setjson();
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启扭蛋");
            setjson();
        }
    }

    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭PcrTool"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shut(GroupMsg msg, MsgSender sender) {
        try {
            On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(false));
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭PcrTool");
            setjson();
        } catch (NullPointerException e) {
            groupPower groupPower = new groupPower();
            groupPower.setOn(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭PcrTool");
            setjson();
        }
    }

    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启PcrTool"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void open(GroupMsg msg, MsgSender sender) {
        try {
            On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setOn(true));
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
            setjson();
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启PcrTool");
            setjson();
        }
    }

    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭提醒买药小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shutbuy(GroupMsg msg, MsgSender sender) {
        try {
            On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setButon(false));
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
            setjson();
        } catch (NullPointerException e) {
            //没这个群的自动都是同意

            groupPower groupPower = new groupPower();
            groupPower.setButon(false);
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭提醒买药小助手");
            setjson();
        }
    }

    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启提醒买药小助手"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void openbuy(GroupMsg msg, MsgSender sender) {
        try {
            On.put(msg.getGroupCode(), On.get(msg.getGroupCode()).setButon(true));
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
            setjson();
        } catch (NullPointerException e) {
            //没这个群的自动都是同意
            groupPower groupPower = new groupPower();
            On.put(msg.getGroupCode(), groupPower);
            sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启提醒买药小助手");
            setjson();
        }
    }

    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#关闭入群欢迎"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void shutwelcome(GroupMsg msg, MsgSender sender) {
        qqGroup byId = qqGroupServiceImpl.getById(msg.getGroupCode());
        if (byId != null) {
            byId.setWelcome_tri(false);
            qqGroupServiceImpl.updateById(byId);
        } else {
            byId = new qqGroup();
            byId.setGroup_number(msg.getGroupCodeNumber());
            byId.setWelcome_tri(false);
            byId.setWelcome(Default_Welcome);
            qqGroupServiceImpl.save(byId);
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已关闭入群欢迎");
    }


    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#设置入群欢迎文本"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void setwelcome(GroupMsg msg, MsgSender sender) {
        String str = msg.getMsg().substring(9).trim();
        qqGroup byId = qqGroupServiceImpl.getById(msg.getGroupCode());
        if (byId != null) {
            byId.setWelcome(str);
            qqGroupServiceImpl.updateById(byId);
        } else {
            byId = new qqGroup();
            byId.setGroup_number(msg.getGroupCodeNumber());
            byId.setWelcome_tri(true);
            byId.setWelcome(str);
            qqGroupServiceImpl.save(byId);
        }
        sender.SENDER.sendGroupMsg(msg, "已更新文本 新文本为：" + str);
    }

    @Check(need = PermEnum.qqAdmin)
    @Listen(MsgGetTypes.groupMsg)
    @Filter(value = {"#开启入群欢迎"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void openwelcome(GroupMsg msg, MsgSender sender) {
        qqGroup byId = qqGroupServiceImpl.getById(msg.getGroupCode());
        if (byId != null) {
            byId.setWelcome_tri(true);
            qqGroupServiceImpl.updateById(byId);
        } else {
            byId = new qqGroup();
            byId.setGroup_number(msg.getGroupCodeNumber());
            byId.setWelcome_tri(true);
            byId.setWelcome(Default_Welcome);
            qqGroupServiceImpl.save(byId);
        }
        sender.SENDER.sendGroupMsg(msg.getGroupCode(), "已开启入群欢迎");
    }

    @Check(need = PermEnum.qqmaster)
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"重载设置"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void reloadconfig(PrivateMsg msg, MsgSender sender) {
        getconfig();
        getgachi();
        getEvent();
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "扭蛋池，马事件已更新,现在设置为：\n" +
                "提醒买药小助手图片名:" + pricnessConfig.getTixingmaiyao() +
                "\n抽卡上限" + pricnessConfig.getGashaponMax() +
                "\n抽卡冷却秒:" + pricnessConfig.getGashaponcool() +
                "\n总开关默认：" + pricnessConfig.isPcrToonon() +
                "\n好像没啥用的开关默认：" + pricnessConfig.isButon() +
                "\n扭蛋开关默认：" + pricnessConfig.isEggon() +
                "\n赛马开关默认：" + pricnessConfig.isHorse() +
                "\nmasterQQ：" + pricnessConfig.getMasterQQ());
    }

    @Check(need = PermEnum.qqmaster)
    @Listen(MsgGetTypes.privateMsg)
    @Filter(value = {"刷新全部签到"}, keywordMatchType = KeywordMatchType.TRIM_EQUALS)
    public void refrashSign(PrivateMsg msg, MsgSender sender) {
        ScoresServiceImpl.clearSign();
        sender.SENDER.sendPrivateMsg(msg.getQQCode(), "已刷新全部签到");
    }


}
