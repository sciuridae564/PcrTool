package cn.sciuridae.aop;

import cn.sciuridae.aop.anno.Check;
import cn.sciuridae.dataBase.service.PcrUnionService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.beans.messages.QQCodeAble;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.PowerType;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cn.sciuridae.constant.pricnessConfig;

@Aspect
@Component
public class CheckImpl {

    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    PcrUnionService pcrUnionServiceImpl;


    @Around("@annotation(cn.sciuridae.aop.anno.Check)")
    public void doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取发送器对象
        MsgSender sender = (MsgSender) joinPoint.getArgs()[1];
        //消息
        Object o = joinPoint.getArgs()[0];

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取注解
        Check HelloWorld = signature.getMethod().getAnnotation(Check.class);
        //权限验证拦截
        switch (HelloWorld.need()) {
            case team://不验证
                break;
            case admin:
                if (o instanceof GroupMsg) {
                    if (!teamMemberServiceImpl.isAdmin((Long.parseLong(((GroupMsg) o).getQQCode())), (Long.parseLong(((GroupMsg) o).getGroupCode())))) {
                        CQCode cqCode_at = CQCodeUtil.build().getCQCode_At(((GroupMsg) o).getQQCode());
                        sender.SENDER.sendGroupMsg(((GroupMsg) o).getGroupCode(), cqCode_at.toString() + " 无工会管理员权限");
                        return;
                    }
                } else if (o instanceof PrivateMsg) {
                    sender.SENDER.sendGroupMsg(((PrivateMsg) o).getQQCode(), "有管理员权限限制的指令只能在相应群里使用");
                    return;
                } else {
                    return;
                }
                break;
            case master:
                if (o instanceof GroupMsg) {
                    if (!pcrUnionServiceImpl.isGroupMaster((Long.parseLong(((GroupMsg) o).getQQCode())), (Long.parseLong(((GroupMsg) o).getGroupCode())))) {
                        CQCode cqCode_at = CQCodeUtil.build().getCQCode_At(((GroupMsg) o).getQQCode());
                        sender.SENDER.sendGroupMsg(((GroupMsg) o).getGroupCode(), cqCode_at.toString() + " 无工会会长权限");
                        return;
                    }
                } else if (o instanceof PrivateMsg) {
                    sender.SENDER.sendGroupMsg(((PrivateMsg) o).getQQCode(), "有会长权限限制的指令只能在相应群里使用");
                    return;
                } else {
                    return;
                }
                break;
            case qqteam://不验证
                break;
            case qqAdmin:
                if (o instanceof GroupMsg) {
                    PowerType powerType = sender.GETTER.getGroupMemberInfo(((GroupMsg) o).getGroupCode(), ((GroupMsg) o).getQQCode()).getPowerType();
                    if (!powerType.isAdmin() &&!powerType.isOwner() && !pricnessConfig.getMasterQQ().equals(((GroupMsg) o).getQQCode())) {
                        CQCode cqCode_at = CQCodeUtil.build().getCQCode_At(((GroupMsg) o).getQQCode());
                        sender.SENDER.sendGroupMsg(((GroupMsg) o).getGroupCode(), cqCode_at.toString() + " 无管理员权限");
                        return;
                    }
                } else if (o instanceof PrivateMsg) {
                    sender.SENDER.sendGroupMsg(((PrivateMsg) o).getQQCode(), "有管理员权限限制的指令只能在相应群里使用");
                    return;
                } else {
                    return;
                }

                break;
            case qqmaster:
                if (o instanceof QQCodeAble) {
                    if (!pricnessConfig.getMasterQQ().equals(((QQCodeAble) o).getQQCode())) {
                        sender.SENDER.sendPrivateMsg(((QQCodeAble) o).getQQCode(), "需要机器人管理员权限");
                        return;
                    }
                } else {
                    return;
                }

                break;
        }

        //通过验证，放行
        joinPoint.proceed();

    }
}
