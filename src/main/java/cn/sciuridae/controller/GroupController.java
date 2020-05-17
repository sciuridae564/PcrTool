package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.teamMember;
import cn.sciuridae.DB.sqLite.DB;
import javafx.beans.property.SimpleStringProperty;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class GroupController {

    @RequestMapping( value="/Group/team")
    public String base(HttpSession session, Model model){
        DB.Power power=(DB.Power)session.getAttribute("token");
        List<teamMember> teamMembers= DB.Instance.getTeamByRowid(power.getteamMemberowId());
        model.addAttribute("teamMembers",teamMembers);
        model.addAttribute("power",power.getPower());
        return "team/Group";
    }

    @GetMapping(value = "/team/{rowid}")
    public String toeditTeam(HttpSession session,@PathVariable("rowid") int rowid, Model model){
        teamMember teamMember=DB.Instance.getteamMemberByrow(rowid);
        model.addAttribute("teamMember",teamMember);
        DB.Power power=(DB.Power)session.getAttribute("token");
        model.addAttribute("power",DB.Instance.getPower(power.getteamMemberowId()));

        return "team/edit";
    }

    @RequestMapping(value = "/team/edit")
    public String editTeam(String QQ,String name, int powerChange,Model model,HttpSession session){
        System.out.println("aaaaaaaaaa");
        System.out.println(QQ);System.out.println(name);System.out.println(powerChange);System.out.println("aaaaaaaaaa");
        DB.Instance.changeName(QQ,name);//先改名
        //后调权限
        switch (powerChange){
            case 1:
                DB.Instance.downAdmin(QQ);
                break;
            case 2:
                DB.Instance.setAdmin(QQ);
                break;
            case 3:
                DB.Power power=(DB.Power)session.getAttribute("token");
                DB.Instance.changeGroupMaster(DB.Instance.searchQQById(power.getteamMemberowId()),QQ);
                power.setPower(2);
                session.setAttribute("token",power);//更新权限
                break;
        }
        return "redirect:/Group/team";
    }

    @GetMapping(value = "/deleteam/{rowid}")
    public String todeleTeam(@PathVariable("rowid") int rowid, Model model){
        String QQ=DB.Instance.searchQQById(rowid);
        if(!DB.Instance.isSuperPower(QQ)){
            System.out.println(DB.Instance.outGroup(QQ));
        }
        return "redirect:/Group/team";
    }
}
