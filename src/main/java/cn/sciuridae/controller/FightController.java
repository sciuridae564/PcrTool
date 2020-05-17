package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.FightStatue;
import cn.sciuridae.DB.bean.Knife;
import cn.sciuridae.DB.sqLite.DB;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static cn.sciuridae.Tools.stringTool.getDate;
import static cn.sciuridae.constant.dateFormat;

@Controller
public class FightController {

    @RequestMapping( value="/Fight/simple")
    public String dosome( HttpSession session ,Model model) {
        DB.Power power= (DB.Power)session.getAttribute("token");
        String qq=DB.Instance.searchQQById(power.getteamMemberowId());
        FightStatue fightStatue= DB.Instance.searchFightStatue(qq);
        if(fightStatue!=null){
            model.addAttribute("fightStatue",fightStatue);//获取现在boss状态
            List<Knife> knives = DB.Instance.searchKnife(qq,null,getDate());//获取今天出刀的数据
            model.addAttribute("knives",knives);//获取刀
            int voidK=3;
            for(Knife k:knives){
                if(k.isComplete())
                    voidK--;
            }
            model.addAttribute("voidK",voidK);//获取现在还剩多少刀
            model.addAttribute("notInFight",true);
            model.addAttribute("Power",power.getPower());
        }else {
            model.addAttribute("Power",power.getPower());
        }
        return "/Fight/SimpleFight";
    }

    @RequestMapping( value="/Fight/List")
    public String list( HttpSession session ,Model model) {
        DB.Power power= (DB.Power)session.getAttribute("token");
        String qq=DB.Instance.searchQQById(power.getteamMemberowId());
        System.out.println("aa");
        if(qq==null){
            model.addAttribute("msg","密匙无效");
            session.setAttribute("token",null);
            return "index";
        }else {
            Collection<Knife> knives= DB.Instance.searchKnifeByQQ(power.getteamMemberowId(),getDate() );
            model.addAttribute("knives",knives);
        }
        return "/main.html";
    }

    @RequestMapping( value="/Fight/static")
    public String stati( HttpSession session ,Model model) {

        return "/Fight/static";
    }

    @RequestMapping( value="/Fight/Start")
    public String start( HttpSession session ,Model model) {

        return "/Fight/SimpleFight";
    }


    @RequestMapping( value="/Fight/end")
    public String end( HttpSession session ,Model model) {

        return "/Fight/SimpleFight";
    }
}
