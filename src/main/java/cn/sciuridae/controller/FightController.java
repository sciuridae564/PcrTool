package cn.sciuridae.controller;

import cn.sciuridae.constant;
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
import cn.sciuridae.listener.prcnessListener;
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
import java.util.*;

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

    public FightController() {
    }

    @RequestMapping({"/Fight/simple"})
    public String dosome(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        model.addAttribute("power", teamMember.getIntPower());
        return "Fight/SimpleFight";
    }

    @RequestMapping({"/Fight/progress"})
    @ResponseBody
    public ProgressI dosome(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Progress progress = this.ProgressServiceImpl.getProgress(teamMember.getGroupQQ());
        ProgressI progressI = new ProgressI(progress, progress.getRemnant() * 100 / constant.BossHpLimit[progress.getSerial() - 1]);
        return progressI;
    }

    @RequestMapping({"/Fight/tree"})
    @ResponseBody
    public List<Tree> tree(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        return this.treeServiceImpl.getTreeByGroup(teamMember.getGroupQQ());
    }

    @RequestMapping({"/Fight/fight"})
    @ResponseBody
    public List<Tree> fight(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        return this.treeServiceImpl.getFightByGroup(teamMember.getGroupQQ());
    }

    @RequestMapping({"/Fight/knive"})
    @ResponseBody
    public List<KnifeList> knive(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        List lists = null;

        try {
            lists = this.knifeListServiceImpl.getKnife(teamMember.getUserQQ(), LocalDateTime.now());
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return lists;
    }

    @RequestMapping({"/Fight/List"})
    public String list(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        model.addAttribute("power", teamMember.getIntPower());
        return "Fight/FightList";
    }

    @GetMapping({"/Fight/selfKnife"})
    @ResponseBody
    public synchronized JSONObject getselfKnife(HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        List<KnifeList> lists = this.knifeListServiceImpl.getKnife(teamMember.getUserQQ(), LocalDateTime.now());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("knife", lists);
        return jsonObject;
    }

    @GetMapping({"/Fight/groupKnife"})
    @ResponseBody
    public synchronized JSONObject getgroupKnife(HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        List<KnifeList> lists = this.knifeListServiceImpl.getKnifeList(group.getGroupQQ(), LocalDateTime.now());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("knife", lists);
        return jsonObject;
    }

    @RequestMapping({"/Fight/GroupKnifeList"})
    @ResponseBody
    public synchronized List<showKnife> getGroupKnife(String start, String end, String QQ, HttpSession session) {
        new JSONObject();
        LocalDateTime[] dates = new LocalDateTime[2];
        LocalDate now = LocalDate.now();
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        Progress progress = this.ProgressServiceImpl.getProgress(group.getGroupQQ());
        List<showKnife> list = new ArrayList();
        LocalDate localDate;
        if (start != null && !Objects.equals(start, "")) {
            localDate = LocalDate.parse(start);
            dates[0] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
        } else {
            LocalDateTime localDateTime = LocalDateTime.now();
            if (localDateTime.getHour() >= 5) {
                localDateTime = localDateTime.withHour(5).withSecond(0).withMinute(0);
            } else {
                localDateTime = localDateTime.withHour(5).withSecond(0).withMinute(0).plusDays(-1L);
            }

            dates[0] = localDateTime;
        }

        if (end != null && !Objects.equals(end, "")) {
            if (Objects.equals(start, end)) {
                localDate = LocalDate.parse(end);
                localDate = localDate.plusDays(1L);
                dates[1] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
            } else {
                localDate = LocalDate.parse(end);
                dates[1] = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
            }
        } else {
            dates[1] = LocalDateTime.now();
        }

        List lists;
        Iterator var13;
        KnifeList k;
        if (null != QQ && !Objects.equals(QQ, "")) {
            String name = this.teamMemberServiceImpl.getName(Long.parseLong(QQ));
            lists = this.knifeListServiceImpl.getKnife(Long.parseLong(QQ), dates[0], dates[1]);
            var13 = lists.iterator();

            while (var13.hasNext()) {
                k = (KnifeList) var13.next();
                showKnife knife = new showKnife(k.getId(), k.getKnifeQQ(), name, k.getHurt(), k.getDate(), k.getComplete(), k.getLoop(), k.getPosition());
                list.add(knife);
            }
        } else {
            lists = this.knifeListServiceImpl.getKnifeList(group.getGroupQQ(), dates[0], dates[1]);
            HashMap<Long, String> nameMap = new HashMap();
            var13 = lists.iterator();

            while (var13.hasNext()) {
                k = (KnifeList) var13.next();
                String name = (String) nameMap.get(k.getKnifeQQ());
                if (name == null) {
                    name = this.teamMemberServiceImpl.getName(k.getKnifeQQ());
                    nameMap.put(k.getKnifeQQ(), name);
                }

                showKnife knife = new showKnife(k.getId(), k.getKnifeQQ(), name, k.getHurt(), k.getDate(), k.getComplete(), k.getLoop(), k.getPosition());
                list.add(knife);
            }
        }

        return list;
    }

    @RequestMapping({"/Fight/delete"})
    @ResponseBody
    public synchronized boolean delete(int id, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        return !teamMember.getPower() ? false : this.knifeListServiceImpl.removeById(id);
    }

    @GetMapping({"/Fight/add"})
    public synchronized String addKnife(HttpSession session, Model model) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        model.addAttribute("power", teamMember.getPower());
        model.addAttribute("userQQ", teamMember.getUserQQ());
        model.addAttribute("time", LocalDateTime.now());
        model.addAttribute("fightStatue", this.ProgressServiceImpl.getProgress(teamMember.getGroupQQ()));
        return "Fight/add";
    }

    @RequestMapping({"/Fight/add1"})
    @ResponseBody
    public synchronized KnifeList addKnife1(int hurt, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        KnifeState knifeState = prcnessListener.toHurt(String.valueOf(teamMember.getGroupQQ()), teamMember.getUserQQ(), hurt,-1, this.knifeListServiceImpl, this.ProgressServiceImpl, this.treeServiceImpl, teamMemberServiceImpl);
        if (knifeState.isOk()) {
            this.botManager.defaultBot().getSender().SENDER.sendGroupMsg(knifeState.getGroupqq(), knifeState.getMsg());
            return knifeState.getKnifeList();
        } else {
            this.botManager.defaultBot().getSender().SENDER.sendGroupMsg(knifeState.getGroupqq(), knifeState.getFailMsg());
            return null;
        }
    }

    @RequestMapping({"/Fight/addforce"})
    public synchronized String addKnifeforce(Model model, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Progress progress = this.ProgressServiceImpl.getProgress(teamMember.getGroupQQ());
        model.addAttribute("fightStatue", progress);
        model.addAttribute("userQQ", teamMember.getUserQQ());
        model.addAttribute("time", LocalDate.now());
        return "/Fight/addforge";
    }

    @RequestMapping({"/Fight/addforce1"})
    @ResponseBody
    public synchronized KnifeList addKnifeforce1(String userQQ, int hurt, int loop, int serial, String time, boolean complete, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (!teamMember.getPower()) {
            return null;
        } else {
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
            } catch (DateTimeParseException var12) {
                return null;
            }

            return this.knifeListServiceImpl.save(knifeList) ? knifeList : null;
        }
    }

    @RequestMapping({"/Fight/edit"})
    public synchronized String edit(int id, Model model, HttpSession session) {
        KnifeList byId = (KnifeList) this.knifeListServiceImpl.getById(id);
        model.addAttribute("knife", byId);
        return "Fight/edit";
    }

    @RequestMapping({"/Fight/edit1"})
    @ResponseBody
    public synchronized boolean edit1(String userQQ, int hurt, int loop, int serial, String time, int id, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (teamMember.getPower()) {
            KnifeList byId = (KnifeList) this.knifeListServiceImpl.getById(id);
            byId.setLoop(loop);
            byId.setPosition(serial);
            byId.setHurt(hurt);
            byId.setKnifeQQ(Long.parseLong(userQQ));
            return this.knifeListServiceImpl.updateById(byId);
        } else {
            return false;
        }
    }

    @RequestMapping({"/Fight/editBoss"})
    public synchronized String editBoss(Model model, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        Progress progress = this.ProgressServiceImpl.getProgress(teamMember.getGroupQQ());
        model.addAttribute("fightStatue", progress);
        return "Fight/editboss";
    }

    @PostMapping({"/Fight/editBoss1"})
    @ResponseBody
    public synchronized boolean editBoss1(int loop, int remnant, int serial, String StartTime, String EndTime, HttpSession session) {
        TeamMemberI teamMember = (TeamMemberI) session.getAttribute("teamMember");
        if (teamMember.getPower()) {
            Progress progress = this.ProgressServiceImpl.getProgress(teamMember.getGroupQQ());
            progress.setLoop(loop);
            progress.setRemnant(remnant);
            progress.setSerial(serial);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            LocalDateTime startlocalDateTime = LocalDateTime.parse(StartTime, dateTimeFormatter);
            LocalDateTime endlocalDateTime = LocalDateTime.parse(EndTime, dateTimeFormatter);
            progress.setStartTime(startlocalDateTime);
            progress.setEndTime(endlocalDateTime);
            int i = this.ProgressServiceImpl.updateFight(progress);
            return i > 0;
        } else {
            return false;
        }
    }
}
