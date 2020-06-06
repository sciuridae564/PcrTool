package cn.sciuridae.controller;

import cn.sciuridae.controller.bean.TeamMemberI;
import cn.sciuridae.dataBase.bean.KnifeList;
import cn.sciuridae.dataBase.bean.PcrUnion;
import cn.sciuridae.dataBase.bean.Progress;
import cn.sciuridae.dataBase.service.KnifeListService;
import cn.sciuridae.dataBase.service.PcrUnionService;
import cn.sciuridae.dataBase.service.ProgressService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;


@Controller
public class BaseController {
    @Autowired
    PcrUnionService pcrUnionServiceImpl;
    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    KnifeListService knifeListServiceImpl;

    //处理登陆来的消息,准备主界面各种消息
    @RequestMapping(value = "/welcome.html")
    public String welcome(HttpSession session, Model Model) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Progress progress = ProgressServiceImpl.getProgress(group.getGroupQQ());
        int knifeNum = knifeListServiceImpl.getKnifeNum(teamMember.getUserQQ(), LocalDateTime.now(), false);
        List<KnifeList> knifeList = null;
        KnifeList topknife = null;
        String topknifename = null;
        if (knifeNum > 0) {
            knifeList = knifeListServiceImpl.getKnifeList(teamMember.getGroupQQ(), LocalDateTime.now());
            topknife = knifeListServiceImpl.getTopKnife(teamMember.getGroupQQ());
            topknifename = teamMemberServiceImpl.getName(topknife.getKnifeQQ());
        }
        Model.addAttribute("username", teamMember.getName());//登陆人名字
        Model.addAttribute("group_teams", teamMemberServiceImpl.getTeamMemberByGroup(teamMember.getGroupQQ()));//登陆人所在组成员列表
        Model.addAttribute("knifeCount", knifeNum);//今日出刀数
        Model.addAttribute("loop", progress.getLoop());
        Model.addAttribute("Serial", progress.getSerial());
        Model.addAttribute("Remnant", progress.getRemnant());
        Model.addAttribute("knives", knifeList);
        Model.addAttribute("topKnife", topknifename);


        return "welcome";
    }


}
