package cn.sciuridae.controller;

import cn.sciuridae.controller.bean.TeamMemberI;
import cn.sciuridae.controller.bean.showTeamMember;
import cn.sciuridae.dataBase.bean.TeamMember;
import cn.sciuridae.dataBase.service.PcrUnionService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import com.alibaba.fastjson.JSONArray;
import com.forte.qqrobot.bot.BotManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class GroupController {
    @Autowired
    PcrUnionService pcrUnionServiceImpl;
    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    private BotManager botManager;

    @RequestMapping(value = "/group/team")
    public String base(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");

        model.addAttribute("power", teamMember.getIntPower());
        return "team/list";
    }

    @GetMapping(value = "/group/list")
    @ResponseBody
    public synchronized String geteam(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        List<TeamMember> list = teamMemberServiceImpl.getTeamMemberByGroup(teamMember.getGroupQQ());

        JSONArray jsonArray = new JSONArray();
        long groupQQ = pcrUnionServiceImpl.getGroupMaster(teamMember.getGroupQQ());
        for (TeamMember t : list) {
            showTeamMember showTeamMember = new showTeamMember();
            showTeamMember.setName(t.getName());
            showTeamMember.setUserQQ(t.getUserQQ());
            if (t.getUserQQ() == groupQQ) {
                showTeamMember.setPower("会长");
            } else if (t.getPower()) {
                showTeamMember.setPower("管理员");
            } else {
                showTeamMember.setPower("组员");
            }
            jsonArray.add(showTeamMember);
        }
        return jsonArray.toJSONString();
    }


    @PostMapping(value = "/group/delete/{qq}")
    @ResponseBody
    public synchronized boolean delete(@PathVariable("qq") long qq, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (!checkGroup(teamMember.getGroupQQ(), teamMember.getUserQQ(), qq)) {
            return false;
        }
        teamMemberServiceImpl.removeById(qq);
        pcrUnionServiceImpl.updateVoidSize(teamMember.getGroupQQ());
        return true;//只要删了一条管他为什么都认为是删ok了

    }


    @RequestMapping(value = "/group/edit")
    public synchronized String edit(@RequestParam("qq") Long qq, Model model, HttpSession session) {
        TeamMember teamMember = teamMemberServiceImpl.getTeamMemberByQQ(qq);
        int power = 1;
        if (pcrUnionServiceImpl.isGroupMaster(qq, teamMember.getGroupQQ())) {
            power = 3;
        } else if (teamMemberServiceImpl.isAdmin(qq, teamMember.getGroupQQ())) {
            power = 2;
        }
        model.addAttribute("power", power);
        model.addAttribute("QQ", teamMember.getUserQQ());
        model.addAttribute("name", teamMember.getName());
        return "team/edit";

    }

    @RequestMapping(value = "/group/edit1")
    @ResponseBody
    public synchronized boolean edit1(@RequestParam("QQ") String QQ, @RequestParam("name") String name, @RequestParam("powerChange") int powerChange, HttpSession session) {
        boolean isTrue = true;
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        long changeQQ = Long.parseLong(QQ);
        //如果更改的qq与自己不在一个工会，说明直接使用了url请求
        if (!checkGroup(teamMember.getGroupQQ(), teamMember.getUserQQ(), changeQQ)) {
            return false;
        }

        //取要改人的权限
        int power = 1;
        if (pcrUnionServiceImpl.isGroupMaster(changeQQ, teamMember.getGroupQQ())) {
            power = 3;
        } else if (teamMemberServiceImpl.isAdmin(changeQQ, teamMember.getGroupQQ())) {
            power = 2;
        }


        //是本人或权限比它高
        if (teamMember.getIntPower() > power || teamMember.getIntPower() == 3) {
            teamMemberServiceImpl.setName(Long.parseLong(QQ), name);
        } else {
            return false;
        }
        //工会长才可以调权限,或者权限没变
        if (power == powerChange) {
            return true;
        } else if (teamMember.getIntPower() == 3) {
            //调权限
            switch (powerChange) {
                case 1:
                    teamMemberServiceImpl.deAdmin(changeQQ);
                    break;
                case 2:
                    teamMemberServiceImpl.setAdmin(changeQQ);
                    break;
                case 3://转让会长
                    pcrUnionServiceImpl.changeGroupMaster(teamMember.getGroupQQ(), changeQQ);
                    break;
            }
            return true;
        }
        return false;
    }

    public boolean checkGroup(long group, long Userqq, long qq) {
        //如果更改的qq与自己不在一个工会，说明直接使用了url请求
        if (teamMemberServiceImpl.getGroupByQQ(qq) != group) {
            String token;
            do {
                token = RandomStringUtils.randomAlphanumeric(20);//密匙生成
                try {
                    Integer tokenNum = teamMemberServiceImpl.getTokenNum(token);
                    if (tokenNum < 1)
                        break;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } while (true);
            teamMemberServiceImpl.updateToken(group, token);
            botManager.defaultBot().getSender().SENDER.sendPrivateMsg(String.valueOf(group),
                    "检测到你的账号进行了非法操作，默认重置了密匙\n新密匙为" + token);
            botManager.defaultBot().getSender().SENDER.sendGroupMsg(String.valueOf(group),
                    "检测到[CQ:at,qq=" + Userqq + "]账号发生了非法操作");
            return false;
        } else {
            return true;
        }
    }
}
