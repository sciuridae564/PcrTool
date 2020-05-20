package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.Group;
import cn.sciuridae.DB.bean.Knife;
import cn.sciuridae.DB.bean.teamMember;
import cn.sciuridae.DB.sqLite.DB;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import java.util.Collection;

import static cn.sciuridae.Tools.stringTool.getDate;


@Controller
public class LoginController {

    @RequestMapping( value="/user/login")
    @ResponseBody
    public boolean dosome(String token, HttpSession session) {
        DB.Power power= DB.Instance.checkPower(token);
        if(power!=null){
            session.setAttribute("token",power);
            Group group=DB.Instance.getGroupByRowid(power.getteamMemberowId());
            session.setAttribute("group",group);
            teamMember teamMember= DB.Instance.getteamMemberByrow(power.getteamMemberowId());
            session.setAttribute("teamMember",teamMember);
            return true;
        }
        return false;

    }

    @RequestMapping( value="/exit")
    public String exit( HttpSession session) {
        session.removeAttribute("token");
        session.removeAttribute("group");
        session.removeAttribute("teamMember");
        return "login";
    }
}