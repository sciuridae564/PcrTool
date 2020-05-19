package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.Group;
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

    @RequestMapping( value="/group/team")
    public String base(HttpSession session, Model model){
        DB.Power power=(DB.Power)session.getAttribute("token");
        model.addAttribute("power",power.getPower());
        return "/team/list";
    }

    @GetMapping(value = "/group/list")
    @ResponseBody
    public List<teamMember> geteam(HttpSession session){
        DB.Power power=(DB.Power)session.getAttribute("token");
        Group group=(Group)session.getAttribute("group");
        List<teamMember> teamMembers= DB.Instance.getTeamByRowid(power.getteamMemberowId());
        int id=DB.Instance.getSuperQQ(group.getId());
        for(teamMember t:teamMembers){
            if(t.getId()==id){
                t.setSuperPower(true);
                break;
            }
        }
        return teamMembers;
    }


    @PostMapping(value = "/group/delete/{rowid}")
    @ResponseBody
    public synchronized boolean delete(@PathVariable("rowid") int rowid){
        String QQ=DB.Instance.searchQQById(rowid);
        return DB.Instance.outGroup(QQ)>0;//只要删了一条管他为什么都认为是删ok了

    }


    @RequestMapping(value = "/group/edit")
    public synchronized String edit(@RequestParam("rowid") int rowid,Model model ,HttpSession session){
        teamMember teamMember=DB.Instance.getteamMemberByrow(rowid);
        if(DB.Instance.isSuperPower(teamMember.getUserQQ())){
            teamMember.setSuperPower(true);
        }
        DB.Power power=(DB.Power)session.getAttribute("token");
        model.addAttribute("teamMember",teamMember);
        model.addAttribute("power",power.getPower());
        return "/team/edit";

    }

    @RequestMapping(value = "/group/edit1")
    @ResponseBody
    public synchronized boolean edit1(@RequestParam("QQ") String QQ,@RequestParam("name") String name ,@RequestParam("powerChange") int powerChange ,HttpSession session){
        boolean isTrue=true;
        DB.Power p= (DB.Power) session.getAttribute("token");
        //取要改人的权限
        int power=DB.Instance.getPower(DB.Instance.searchTeamMemberowIdByQQ(QQ));
        //是本人或权限比它高
       if(p.getPower()>power|| DB.Instance.searchQQById(p.getteamMemberowId()).equals(QQ)){
           DB.Instance.changeName(QQ,name);
       }else {
           return false;
       }
       //工会长才可以调权限,或者权限没变
        if( power==powerChange){
            return true;
        }else if(p.getPower()==3 ){
            //调权限
            switch (powerChange){
                case 1:
                    DB.Instance.downAdmin(QQ);
                    break;
                case 2:
                    DB.Instance.setAdmin(QQ);
                    break;
                case 3://转让会长
                    String oldQQ= DB.Instance.searchQQById(p.getteamMemberowId());
                    DB.Instance.changeGroupMaster(oldQQ,QQ);
                    break;
            }
            return true;
        }

        return false;

    }
}
