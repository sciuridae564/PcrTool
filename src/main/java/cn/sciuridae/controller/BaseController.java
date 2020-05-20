package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.FightStatue;
import cn.sciuridae.DB.bean.Group;
import cn.sciuridae.DB.bean.Knife;
import cn.sciuridae.DB.sqLite.DB;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import static cn.sciuridae.Tools.stringTool.getDate;
import static cn.sciuridae.Tools.stringTool.getLastDate;

@Controller
public class BaseController {

    //处理登陆来的消息,准备主界面各种消息
    @RequestMapping( value="/welcome.html")
    public String welcome(HttpSession session,Model Model){
        DB.Power power=(DB.Power)session.getAttribute("token");
        Group group = (Group) session.getAttribute("group");
        FightStatue fightStatue= DB.Instance.searchFightStatue(group.getId());

        Model.addAttribute("username", DB.Instance.getNameByRowId(power.getteamMemberowId()));//登陆人名字
        Model.addAttribute("group", group);//登陆人所在组
        Model.addAttribute("group_teams", DB.Instance.getTeammembers(power.getteamMemberowId()));//登陆人所在组成员列表
        Model.addAttribute("knifeCount", DB.Instance.searchKnifeCount(group.getId(),getDate()));//今日出刀数
        Model.addAttribute("loop",fightStatue.getLoop() );
        Model.addAttribute("Serial",fightStatue.getSerial() );
        Model.addAttribute("Remnant",fightStatue.getRemnant() );
        Model.addAttribute("knives", DB.Instance.searchKnife(null,group.getGroupid(),getLastDate()));
        Knife knife= DB.Instance.getTopKnife(group.getId(),getLastDate());
        if(knife!=null){knife.setName(DB.Instance.searchName(knife.getKnifeQQ()));}
        Model.addAttribute("topKnife",knife );


        return "/welcome";
    }


}
