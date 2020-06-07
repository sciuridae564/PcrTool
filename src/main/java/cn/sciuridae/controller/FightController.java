package cn.sciuridae.controller;

import cn.sciuridae.controller.bean.ProgressI;
import cn.sciuridae.controller.bean.TeamMemberI;
import cn.sciuridae.controller.bean.showKnife;
import cn.sciuridae.dataBase.bean.KnifeList;
import cn.sciuridae.dataBase.bean.PcrUnion;
import cn.sciuridae.dataBase.bean.Progress;
import cn.sciuridae.dataBase.bean.Tree;
import cn.sciuridae.dataBase.service.KnifeListService;
import cn.sciuridae.dataBase.service.ProgressService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import cn.sciuridae.dataBase.service.TreeService;
import cn.sciuridae.listener.KnifeState;
import com.alibaba.fastjson.JSONObject;
import com.forte.qqrobot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static cn.sciuridae.constant.BossHpLimit;
import static cn.sciuridae.listener.prcnessListener.toHurt;

@Controller
public class FightController {

    @Autowired
    KnifeListService knifeListServiceImpl;
    @Autowired
    ProgressService ProgressServiceImpl;
    @Autowired
    TreeService treeServiceImpl;
    @Autowired
    TeamMemberService teamMemberServiceImpl;
    @Autowired
    private BotManager botManager;

    @RequestMapping(value = "/Fight/simple")
    public String dosome(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        model.addAttribute("power", teamMember.getIntPower());//设置权限
        return "Fight/SimpleFight";
    }

    @RequestMapping(value = "/Fight/progress")
    @ResponseBody
    public ProgressI dosome(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Progress progress = ProgressServiceImpl.getProgress(teamMember.getGroupQQ());
        ProgressI progressI = new ProgressI(progress, (progress.getRemnant() * 100) / BossHpLimit[progress.getSerial() - 1]);
        return progressI;
    }

    @RequestMapping(value = "/Fight/tree")
    @ResponseBody
    public List<Tree> tree(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Tree tree;
        return treeServiceImpl.getTreeByGroup(teamMember.getGroupQQ());
    }

    @RequestMapping(value = "/Fight/fight")
    @ResponseBody
    public List<Tree> fight(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        return treeServiceImpl.getFightByGroup(teamMember.getGroupQQ());
    }

    @RequestMapping(value = "/Fight/knive")
    @ResponseBody
    public List<KnifeList> knive(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        return knifeListServiceImpl.getKnife(teamMember.getUserQQ(), LocalDateTime.now());
    }

    @RequestMapping(value = "/Fight/List")
    public String list(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        model.addAttribute("power", teamMember.getIntPower());//设置权限
        return "Fight/FightList";
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


    @RequestMapping(value = "/Fight/GroupKnifeList")
    @ResponseBody
    public synchronized List<showKnife> getGroupKnife(String start, String end, String QQ, HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        LocalDateTime[] dates = new LocalDateTime[2];
        LocalDate now = LocalDate.now();
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        Progress progress = ProgressServiceImpl.getProgress(group.getGroupQQ());
        List<KnifeList> lists;
        List<showKnife> list = new ArrayList<>();
        if (start != null && !Objects.equals(start, "")) {
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

        if (end != null && !Objects.equals(end, "")) {
            if (Objects.equals(start, end)) {
                LocalDate localDate = LocalDate.parse(end);
                localDate = localDate.plusDays(1);
                dates[1] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
            } else {
                LocalDate localDate = LocalDate.parse(end);
                dates[1] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
            }
        } else {
            dates[1] = LocalDateTime.now();
        }

        if (null != QQ && !Objects.equals(QQ, "")) {
            String name = teamMemberServiceImpl.getName(Long.parseLong(QQ));
            lists = knifeListServiceImpl.getKnife(Long.parseLong(QQ), dates[0], dates[1]);
            for (KnifeList k : lists) {
                showKnife knife = new showKnife(k.getId(), k.getKnifeQQ(), name, k.getHurt(), k.getDate(), k.getComplete(), k.getLoop(), k.getPosition());
                list.add(knife);
            }
        } else {
            lists = knifeListServiceImpl.getKnifeList(group.getGroupQQ(), dates[0], dates[1]);
            HashMap<Long, String> nameMap = new HashMap<>();
            for (KnifeList k : lists) {
                String name = nameMap.get(k.getKnifeQQ());
                if (name == null) {
                    name = teamMemberServiceImpl.getName(k.getKnifeQQ());
                    nameMap.put(k.getKnifeQQ(), name);
                }
                showKnife knife = new showKnife(k.getId(), k.getKnifeQQ(), name, k.getHurt(), k.getDate(), k.getComplete(), k.getLoop(), k.getPosition());
                list.add(knife);
            }

        }
        return list;
    }

    @RequestMapping(value = "/Fight/delete")
    @ResponseBody
    public synchronized boolean delete(int id, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (!teamMember.getPower()) {
            return false;
        }

        return knifeListServiceImpl.removeById(id);
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
    public synchronized KnifeList addKnife1(int hurt, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");

        KnifeState knifeState = toHurt(teamMember.getGroupQQ(), teamMember.getUserQQ(), hurt, knifeListServiceImpl, ProgressServiceImpl, treeServiceImpl);
        if (knifeState.isOk()) {
            botManager.defaultBot().getSender().SENDER.sendGroupMsg(String.valueOf(teamMember.getGroupQQ()), knifeState.getMsg());
            return knifeState.getKnifeList();
        } else {
            botManager.defaultBot().getSender().SENDER.sendGroupMsg(String.valueOf(teamMember.getGroupQQ()), knifeState.getFailMsg());
            return null;
        }

    }

    //添加一个出刀信息
    @RequestMapping(value = "/Fight/addforce")
    public synchronized String addKnifeforce(Model model, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Progress progress = ProgressServiceImpl.getProgress(teamMember.getGroupQQ());
        model.addAttribute("fightStatue", progress);
        model.addAttribute("userQQ", teamMember.getUserQQ());
        model.addAttribute("time", LocalDate.now());
        return "/Fight/addforge";
    }

    //添加一个出刀信息
    @RequestMapping(value = "/Fight/addforce1")
    @ResponseBody
    public synchronized KnifeList addKnifeforce1(String userQQ, int hurt, int loop, int serial, String time, boolean complete, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (!teamMember.getPower()) {
            return null;
        }
        KnifeList knifeList = new KnifeList();
        knifeList.setKnifeQQ(Long.parseLong(userQQ));
        knifeList.setHurt(hurt);
        knifeList.setLoop(loop);
        knifeList.setPosition(serial);
        knifeList.setComplete(complete);
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
            knifeList.setDate(localDateTime);
        } catch (DateTimeParseException e) {
            return null;
        }

        if (knifeListServiceImpl.save(knifeList)) {
            return knifeList;
        } else {
            return null;
        }

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
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            LocalDateTime startlocalDateTime = LocalDateTime.parse(StartTime, dateTimeFormatter);
            LocalDateTime endlocalDateTime = LocalDateTime.parse(EndTime, dateTimeFormatter);

            progress.setStartTime(startlocalDateTime);
            progress.setEndTime(endlocalDateTime);

            int i = ProgressServiceImpl.updateFight(progress);
            return i > 0;
        } else {
            return false;
        }
    }
}
