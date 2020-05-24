package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.FightStatue;
import cn.sciuridae.DB.bean.Group;
import cn.sciuridae.DB.bean.Knife;
import cn.sciuridae.DB.bean.teamMember;
import cn.sciuridae.DB.sqLite.DB;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

import static cn.sciuridae.Tools.stringTool.getDate;
import static cn.sciuridae.constant.BossHpLimit;

@Controller
public class FightController {

    @RequestMapping( value="/Fight/simple")
    public String dosome( HttpSession session ,Model model) {
        DB.Power power= (DB.Power)session.getAttribute("token");
        model.addAttribute("Power",power.getPower());//设置权限
        return "Fight/SimpleFight";
    }

    @RequestMapping( value="/Fight/List")
    public String list(HttpSession session ,Model model) {
        DB.Power power= (DB.Power)session.getAttribute("token");
        model.addAttribute("Power",power.getPower());//设置权限
        return "Fight/FightList";
    }

    //正在出刀数据请求
    @GetMapping(value = "/Fight/OutKnife")
    @ResponseBody
    public synchronized List<String[]> getOutKnife(HttpSession session ){
        Group group = (Group) session.getAttribute("group");
        List<String[]> timeline=new ArrayList<>();List<String> ids=DB.Instance.searchOutKnife(group.getGroupid());
        if(ids!=null){
            for (int i=0;i<ids.size();i++){
                String[] id =new String[2];
                id[0]=ids.get(i);id[1]= DB.Instance.searchName(ids.get(i));
                timeline.add(id);
            }
        }
        return timeline;
    }

    //挂树数据查询请求
    @GetMapping(value = "/Fight/tree")
    @ResponseBody
    public synchronized List<String[]> gettree(HttpSession session ){
        Group group = (Group) session.getAttribute("group");
        List<String> list=DB.Instance.searchTree(group.getGroupid());
        List<String[]> list1=new ArrayList<>();
        if(list!=null){
            for (int i=0;i<list.size();i++){
                String[] s=new String[2];s[0]=list.get(i);s[1]=DB.Instance.searchName(list.get(i));
                list1.add(s);
            }
        }
        return list1;
    }

    //自己已出刀数据请求
    @GetMapping(value = "/Fight/selfKnife")
    @ResponseBody
    public synchronized List<Knife> getselfKnife(HttpSession session ){
        teamMember teamMember= (teamMember)session.getAttribute("teamMember");
        List<Knife> knives = DB.Instance.searchKnife(teamMember.getUserQQ(),null,getDate());
        return knives;
    }

    //全体已出刀数据请求
    @GetMapping(value = "/Fight/groupKnife")
    @ResponseBody
    public synchronized List<Knife> getgroupKnife(HttpSession session ){
        Group group = (Group) session.getAttribute("group");
        List<Knife>  knives=DB.Instance.searchKnife(null,group.getGroupid(),getDate());

        return knives;
    }

    //获取现在boss状态
    @GetMapping(value = "/Fight/bossFight")
    @ResponseBody
    public synchronized FightStatue getFightStatue(HttpSession session ){
        Group group = (Group) session.getAttribute("group");
        FightStatue fightStatue= DB.Instance.searchFightStatue(group.getId());
        fightStatue.setDem((fightStatue.getRemnant()*100)/BossHpLimit[fightStatue.getSerial()-1]);//算出血量百分比
        return fightStatue;
    }


    @GetMapping(value = "/Fight/GroupKnifeList")
    @ResponseBody
    public synchronized List<Knife> getGroupKnife(String start,String end,String QQ, HttpSession session){
        DB.Power power= (DB.Power)session.getAttribute("token");
        List<Knife> knives;
        if(QQ.length()>0){//有qq
            knives=DB.Instance.searchKnifeByQQ(QQ,start.substring(2),end.substring(2) );
        }else {
            knives= DB.Instance.searchKnifeByQQ(power.getteamMemberowId(),start.substring(2),end.substring(2) );
        }

        //处理一下，把昵称整进去
        List<teamMember> teamMembers=DB.Instance.getTeammembers(power.getteamMemberowId());
        Map<String,String> map=new HashMap<>();
       for (teamMember teamMember:teamMembers){
           map.put(teamMember.getUserQQ(),teamMember.getName());
       }
        for (Knife knife:knives){
            knife.setName(map.get(knife.getKnifeQQ()));
            System.out.println(knife);
        }
        return knives;
    }

    @PostMapping(value = "/Fight/delete/{rowid}")
    @ResponseBody
    public synchronized boolean delete(@PathVariable("rowid") int rowid){
        return DB.Instance.deleteKnife(rowid);
    }

    //添加一个出刀信息
    @GetMapping(value = "/Fight/add")
    public synchronized String addKnife(HttpSession session ,Model model){
        DB.Power power= (DB.Power)session.getAttribute("token");
        Group group = (Group) session.getAttribute("group");
        FightStatue fightStatue= DB.Instance.searchFightStatue(group.getId());
        model.addAttribute("power",power.getPower());//权限
        model.addAttribute("userQQ", DB.Instance.searchQQById(power.getteamMemberowId()));
        model.addAttribute("time", "20"+getDate());
        model.addAttribute("fightStatue", fightStatue);

        return  "Fight/add";
    }

    //添加一个出刀信息
    @RequestMapping(value = "/Fight/add1")
    @ResponseBody
    public synchronized boolean addKnife1(String userQQ,int hurt,int loop,int serial,String time){
        if(DB.Instance.addKnife(userQQ,loop,serial,hurt,time.substring(2))){
            return  true;
        }else {
            return  false;
        }
    }

    @RequestMapping(value = "/Fight/edit")
    public synchronized String edit( int id,Model model,HttpSession session){

        Knife knife=DB.Instance.getKnife(id);knife.setDate("20"+knife.getDate());
        model.addAttribute("knife",knife);

        return  "Fight/edit";
    }

    @RequestMapping(value = "/Fight/edit1")
    @ResponseBody
    public synchronized boolean edit1(String userQQ,int hurt,int loop,int serial,String time,int id){
        if(DB.Instance.updateKnife(userQQ,hurt,loop,serial,time.substring(2),id)>0){
            return  true;
        }else {
            return  false;
        }
    }


    //更改boss数据
    @RequestMapping(value = "/Fight/editBoss")
    public synchronized String editBoss( Model model,HttpSession session){
        session.getAttribute("group");
        Group group = (Group) session.getAttribute("group");
        FightStatue fightStatue= DB.Instance.searchFightStatue(group.getId());
        fightStatue.setStartTime("20"+fightStatue.getStartTime());
        fightStatue.setEndTime("20"+fightStatue.getEndTime());
        model.addAttribute("fightStatue",fightStatue);
        return  "Fight/editboss";
    }

    @PostMapping(value = "/Fight/editBoss1")
    @ResponseBody
    public synchronized boolean editBoss1(int loop,int remnant,int serial,String StartTime,String EndTime,HttpSession session){
        Group group = (Group) session.getAttribute("group");
        return DB.Instance.changeBoss(group.getGroupid(),loop,serial,remnant,StartTime.substring(2),EndTime.substring(2));
    }
}
