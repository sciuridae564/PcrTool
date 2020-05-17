package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.Knife;
import cn.sciuridae.DB.sqLite.DB;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import java.util.Collection;

import static cn.sciuridae.Tools.stringTool.getDate;


@Controller
public class LoginController {

    @RequestMapping( value="/user/login")
    public String dosome(String token,
                         Model model, HttpSession session) {
        DB.Power power= DB.Instance.checkPower(token);
        if(power!=null){
            session.setAttribute("token",power);
            return "redirect:/Base/start" ;
        }else {
            model.addAttribute("msg","密匙无效");
            return "index";
        }
    }

    @RequestMapping( value="/exit")
    public String exit( HttpSession session) {
        session.removeAttribute("token");
        return "index";
    }
}