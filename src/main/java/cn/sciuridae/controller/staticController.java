package cn.sciuridae.controller;

import cn.sciuridae.DB.bean.Group;
import cn.sciuridae.DB.bean.Knife;
import cn.sciuridae.DB.sqLite.DB;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

import static cn.sciuridae.Tools.stringTool.getDate;

@Controller
public class staticController {


    //返回那天的打全刀的人
    @RequestMapping( value="/Fight/static")
    public String gete(HttpSession session, Model model){
        Group group = (Group) session.getAttribute("group");
        Map<String,  List<Knife>> map= DB.Instance.searchKnife(group.getId(),getDate());
        Set<String>  set=map.keySet();
        List<String> list=new ArrayList<>();//出完全刀的人
        long sum=0;//总伤害
        int min =10000,max=0;//最小王编号和最大王编号

        for (String o:set){
            List<Knife> list1=map.get(o);
            if(list1.size()>2){
                list.add(o);
            }

            for(Knife k:list1){
                if(k.getNo()>max){max=k.getNo();}
                if(k.getNo()<min){min=k.getNo();}
                sum+=k.getHurt();
            }
        }
        model.addAttribute("allKnifecount",list.size());
        model.addAttribute("hurtSum",sum);
        model.addAttribute("kingcount",((max/10)-(min/10))*5+max%10-min%10);

        return "Fight/static";
    }

    //返回那天的刀
    @RequestMapping( value="/static/Knifelist")
    @ResponseBody
    public JSONObject getKnife(String time, HttpSession session){
        Group group = (Group) session.getAttribute("group");
        List<Knife> list;
        if(time==null||time.length()==0){
            list=DB.Instance.getAllKnife(group.getId(),getDate());
        }else {
            list=DB.Instance.getAllKnife(group.getId(),time.substring(2));
        }
        List<String> categories=new LinkedList<>();
        List<Integer> data=new LinkedList<>();
        for (Knife k:list){
            categories.add(DB.Instance.searchName(k.getKnifeQQ()));
            data.add(k.getHurt());
        }
        ReportDataBean reportDataBean=new ReportDataBean(categories,data);
        JSONObject jsonObj = (JSONObject) JSON.toJSON(reportDataBean);
        return jsonObj;//现在就是今天的刀
    }

    //返回那天的打全刀的人
    @RequestMapping( value="/static/normal")
    @ResponseBody
    public JSONObject gete(String time, HttpSession session){
        Group group = (Group) session.getAttribute("group");
        Map<String,  List<Knife>> map= DB.Instance.searchKnife(group.getId(),time.substring(2));
        Set<String>  set=map.keySet();
        List<String> list=new ArrayList<>();//出完全刀的人
        long sum=0;//总伤害
        int min =10000,max=0;//最小王编号和最大王编号

        for (String o:set){
            List<Knife> list1=map.get(o);
            if(list1.size()>2){
                list.add(o);
            }

            for(Knife k:list1){
                if(k.getNo()>max){max=k.getNo();}
                if(k.getNo()<min){min=k.getNo();}
                sum+=k.getHurt();
            }
        }
        ReportDataBean1 ReportDataBean1=new ReportDataBean1(list.size(),sum,((max/10)-(min/10))*5+max%10-min%10);
        JSONObject jsonObj = (JSONObject) JSON.toJSON(ReportDataBean1);

        return jsonObj;
    }


    public class ReportDataBean1{
        private int allKnifecount;
        private long hurtSum;
        private int kingcount;

        public ReportDataBean1(int allKnifecount, long hurtSum, int kingcount) {
            this.allKnifecount = allKnifecount;
            this.hurtSum = hurtSum;
            this.kingcount = kingcount;
        }

        public int getAllKnifecount() {
            return allKnifecount;
        }

        public void setAllKnifecount(int allKnifecount) {
            this.allKnifecount = allKnifecount;
        }

        public long getHurtSum() {
            return hurtSum;
        }

        public void setHurtSum(long hurtSum) {
            this.hurtSum = hurtSum;
        }

        public int getKingcount() {
            return kingcount;
        }

        public void setKingcount(int kingcount) {
            this.kingcount = kingcount;
        }
    }

    public class ReportDataBean {
        private List<String> categories;
        private List<Integer> data;

        public ReportDataBean(List<String> categories, List<Integer> data) {
            this.categories = categories;
            this.data = data;
        }

        public List<String> getCategories() {
            return categories;
        }
        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
        public List<Integer> getData() {
            return data;
        }
        public void setData(List<Integer> data) {
            this.data = data;
        }
    }
}
