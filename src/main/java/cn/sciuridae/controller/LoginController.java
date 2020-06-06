package cn.sciuridae.controller;

import cn.sciuridae.controller.bean.TeamMemberI;
import cn.sciuridae.dataBase.bean.PcrUnion;
import cn.sciuridae.dataBase.bean.TeamMember;
import cn.sciuridae.dataBase.service.PcrUnionService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import com.forte.qqrobot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;


@Controller
public class LoginController {
    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    PcrUnionService pcrUnionServiceImpl;
    @Autowired
    private BotManager botManager;

    @GetMapping(value = "/user/check")
    @ResponseBody
    public boolean dosome(@RequestParam("token") String token, HttpSession session) {
        TeamMember teamMember = teamMemberServiceImpl.getTeamMemberBytoken(token);
        if (teamMember != null) {
            TeamMemberI teamMemberi = new TeamMemberI(teamMember, pcrUnionServiceImpl.isGroupMaster(teamMember.getUserQQ(), teamMember.getGroupQQ()));
            session.setAttribute("teamMember", teamMemberi);
            PcrUnion group = pcrUnionServiceImpl.getGroup(teamMember.getUserQQ());
            session.setAttribute("group", group);
            botManager.defaultBot().getSender().SENDER.sendPrivateMsg(String.valueOf(teamMember.getUserQQ()), "登陆了网页" + LocalDateTime.now());
            return true;
        }
        return false;

    }

    @RequestMapping(value = "/exit")
    public String exit(HttpSession session) {
        session.removeAttribute("teamMember");
        session.removeAttribute("group");
        return "login";
    }
}