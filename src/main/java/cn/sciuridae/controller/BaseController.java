package cn.sciuridae.controller;

import cn.sciuridae.DB.sqLite.DB;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class BaseController {

    @RequestMapping( value="/Base")
    public String base(){
        return "";
    }

    //处理登陆来的消息,准备主界面各种消息
    @RequestMapping( value="/Base/start")
    public String baseStart(HttpSession session,Model Model){
        DB.Power power=(DB.Power)session.getAttribute("token");
//        session.setAttribute("username", DB.Instance.getNameByRowId(power.getteamMemberowId()));//这人名字
//        session.setAttribute("group", DB.Instance.getGroupByRowid(power.getteamMemberowId()));//这人团信息

        Model.addAttribute("username", DB.Instance.getNameByRowId(power.getteamMemberowId()));
        Model.addAttribute("group", DB.Instance.getGroupByRowid(power.getteamMemberowId()));
        return "indexboard";
    }
}
