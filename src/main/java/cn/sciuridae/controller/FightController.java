package cn.sciuridae.controller;

import cn.sciuridae.controller.bean.ProgressI;
import cn.sciuridae.controller.bean.TeamMemberI;
import cn.sciuridae.dataBase.bean.KnifeList;
import cn.sciuridae.dataBase.bean.PcrUnion;
import cn.sciuridae.dataBase.bean.Progress;
import cn.sciuridae.dataBase.bean.Tree;
import cn.sciuridae.dataBase.service.KnifeListService;
import cn.sciuridae.dataBase.service.ProgressService;
import cn.sciuridae.dataBase.service.TreeService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.sciuridae.constant.BossHpLimit;

@Controller
public class FightController {

    @Autowired
    KnifeListService knifeListServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    TreeService treeServiceImpl;

    @RequestMapping(value = "/Fight/simple")
    public String dosome(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        model.addAttribute("Power", teamMember.getIntPower());//设置权限
        return "Fight/SimpleFight";
    }

    @RequestMapping(value = "/Fight/List")
    public String list(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        model.addAttribute("Power", teamMember.getIntPower());//设置权限
        return "Fight/FightList";
    }

    //正在出刀数据请求 返回json字符串 { qq：，name：}
    @GetMapping(value = "/Fight/OutKnife")
    @ResponseBody
    public synchronized JSONObject getOutKnife(HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        List<Tree> list = treeServiceImpl.getTreeByGroup(group.getGroupQQ());
        JSONArray name = new JSONArray();
        JSONArray qq = new JSONArray();
        if (list != null) {
            for (Tree tree : list) {
                name.add(tree.getName());
                qq.add(tree.getTeamQQ());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("qq", qq);
        return jsonObject;
    }

    //挂树数据查询请求
    @GetMapping(value = "/Fight/tree")
    @ResponseBody
    public synchronized JSONObject gettree(HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        List<Tree> list = treeServiceImpl.getFightByGroup(group.getGroupQQ());
        JSONArray name = new JSONArray();
        JSONArray qq = new JSONArray();
        if (list != null) {
            for (Tree tree : list) {
                name.add(tree.getName());
                qq.add(tree.getTeamQQ());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("qq", qq);
        return jsonObject;
    }

    //自己已出刀数据请求
    @GetMapping(value = "/Fight/selfKnife")
    @ResponseBody
    public synchronized JSONObject getselfKnife(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        List<KnifeList> lists = knifeListServiceImpl.getKnife(teamMember.getUserQQ(), LocalDateTime.now());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("knife", lists);

        return jsonObject;
    }

    //全体已出刀数据请求
    @GetMapping(value = "/Fight/groupKnife")
    @ResponseBody
    public synchronized JSONObject getgroupKnife(HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        List<KnifeList> lists = knifeListServiceImpl.getKnifeList(group.getGroupQQ(), LocalDateTime.now());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("knife", lists);

        return jsonObject;
    }

    //获取现在boss状态
    @GetMapping(value = "/Fight/bossFight")
    @ResponseBody
    public synchronized ProgressI getFightStatue(HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        Progress progress = ProgressServiceImpl.getProgress(group.getGroupQQ());
        ProgressI progressI = new ProgressI(progress, (progress.getRemnant() * 100) / BossHpLimit[progress.getSerial() - 1]);

        //算出血量百分比
        return progressI;
    }


    @GetMapping(value = "/Fight/GroupKnifeList")
    @ResponseBody
    public synchronized JSONObject getGroupKnife(String start, String end, String QQ, HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        LocalDateTime[] dates = new LocalDateTime[2];
        LocalDate now = LocalDate.now();
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        Progress progress = ProgressServiceImpl.getProgress(group.getGroupQQ());
        List<KnifeList> lists;
        if (start != null) {
            LocalDate localDate = LocalDate.parse(start);
            dates[0] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            if (localDateTime.getHour() >= 5) {
                localDateTime = localDateTime.withHour(5).withSecond(0).withMinute(0);
            } else {
                localDateTime = localDateTime.withHour(5).withSecond(0).withMinute(0).plusDays(-1);
            }
            dates[0] = localDateTime;
        }

        if (end != null) {
            if (Objects.equals(start, end)) {
                LocalDate localDate = LocalDate.parse(end);
                localDate.plusDays(1);
                dates[1] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0);
            } else {
                LocalDate localDate = LocalDate.parse(end);
                dates[1] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 0, 0);
            }
        } else {
            dates[1] = LocalDateTime.now();
        }

        if (null != QQ) {
            lists = knifeListServiceImpl.getKnife(Long.parseLong(QQ), dates[0], dates[1]);
        } else {
            lists = knifeListServiceImpl.getKnifeList(group.getGroupQQ(), dates[0], dates[1]);
        }
        jsonObject.put("knife", lists);
        return jsonObject;
    }

    @PostMapping(value = "/Fight/delete/{rowid}")
    @ResponseBody
    public synchronized boolean delete(@PathVariable("rowid") int rowid, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (!teamMember.getPower()) {
            return false;
        }

        return knifeListServiceImpl.removeById(rowid);
    }

    //添加一个出刀信息
    @GetMapping(value = "/Fight/add")
    public synchronized String addKnife(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");


        model.addAttribute("power", teamMember.getPower());//权限
        model.addAttribute("userQQ", teamMember.getUserQQ());
        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("fightStatue", ProgressServiceImpl.getProgress(teamMember.getGroupQQ()));

        return "Fight/add";
    }

    //添加一个出刀信息
    @RequestMapping(value = "/Fight/add1")
    @ResponseBody
    public synchronized boolean addKnife1(String userQQ, int hurt, int loop, int serial, String time, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (teamMember.getUserQQ() != Long.parseLong(userQQ) && !teamMember.getPower()) {
            return false;
        }
        KnifeList knifeList = new KnifeList();
        knifeList.setKnifeQQ(Long.parseLong(userQQ));
        knifeList.setHurt(hurt);
        knifeList.setLoop(loop);
        knifeList.setPosition(serial);

        knifeList.setDate(LocalDateTime.now());
        return knifeListServiceImpl.save(knifeList);
    }

    @RequestMapping(value = "/Fight/edit")
    public synchronized String edit(int id, Model model, HttpSession session) {
        KnifeList byId = knifeListServiceImpl.getById(id);
        model.addAttribute("knife", byId);
        return "Fight/edit";
    }

    @RequestMapping(value = "/Fight/edit1")
    @ResponseBody
    public synchronized boolean edit1(String userQQ, int hurt, int loop, int serial, String time, int id, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (teamMember.getPower()) {
            KnifeList byId = knifeListServiceImpl.getById(id);
            byId.setLoop(loop);
            byId.setPosition(serial);
            byId.setHurt(hurt);
            byId.setKnifeQQ(Long.parseLong(userQQ));
            return knifeListServiceImpl.updateById(byId);
        } else {
            return false;
        }
    }


    //更改boss数据
    @RequestMapping(value = "/Fight/editBoss")
    public synchronized String editBoss(Model model, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Progress progress = ProgressServiceImpl.getProgress(teamMember.getGroupQQ());

        model.addAttribute("fightStatue", progress);
        return "Fight/editboss";
    }

    @PostMapping(value = "/Fight/editBoss1")
    @ResponseBody
    public synchronized boolean editBoss1(int loop, int remnant, int serial, String StartTime, String EndTime, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (teamMember.getPower()) {
            Progress progress = ProgressServiceImpl.getProgress(teamMember.getGroupQQ());
            progress.setLoop(loop);
            progress.setRemnant(remnant);
            progress.setSerial(serial);
            LocalDate startTimelocalDate = LocalDate.parse(StartTime);
            LocalDate endTimelocalDate = LocalDate.parse(EndTime);
            LocalDateTime startlocalDateTime = LocalDateTime.of(startTimelocalDate.getYear(), startTimelocalDate.getMonth(), startTimelocalDate.getDayOfMonth(), 5, 0);
            LocalDateTime endlocalDateTime = LocalDateTime.of(endTimelocalDate.getYear(), endTimelocalDate.getMonth(), endTimelocalDate.getDayOfMonth(), 5, 0);

            progress.setStartTime(startlocalDateTime);
            progress.setEndTime(endlocalDateTime);

            int i = ProgressServiceImpl.updateFight(progress);
            return i > 0;
        } else {
            return false;
        }
    }
}
