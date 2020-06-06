package cn.sciuridae.controller;

import cn.sciuridae.dataBase.bean.KnifeList;
import cn.sciuridae.dataBase.bean.PcrUnion;
import cn.sciuridae.dataBase.service.KnifeListService;
import cn.sciuridae.dataBase.service.PcrUnionService;
import cn.sciuridae.dataBase.service.TeamMemberService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;


@Controller
public class staticController {
    @Autowired
    PcrUnionService pcrUnionServiceImpl;
    @Autowired
    KnifeListService knifeListServiceImpl;
    @Autowired
    TeamMemberService teamMemberServiceImpl;

    @RequestMapping(value = "/Fight/static")
    public String gete(HttpSession session, Model model) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        List<KnifeList> lists = knifeListServiceImpl.getKnifeList(group.getGroupQQ(), LocalDateTime.now());
        long sum = 0;//总伤害
        int min = 10000, max = 0;//最小王编号和最大王编号
        if (lists != null && lists.size() > 0) {
            for (KnifeList knifeList : lists) {
                sum += knifeList.getHurt();
                if (max < knifeList.getLoop() * 10 + knifeList.getPosition()) {
                    max = knifeList.getLoop() * 10 + knifeList.getPosition();
                }
                if (min > knifeList.getLoop() * 10 + knifeList.getPosition()) {
                    min = knifeList.getLoop() * 10 + knifeList.getPosition();
                }
            }
            model.addAttribute("allKnifecount", lists.size());
            model.addAttribute("hurtSum", sum);
            model.addAttribute("kingcount", ((max / 10) - (min / 10)) * 5 + max % 10 - min % 10);
        } else {
            model.addAttribute("allKnifecount", 0);
            model.addAttribute("hurtSum", 0);
            model.addAttribute("kingcount", 0);
        }

        return "Fight/static";
    }

    //返回那天的刀
    @RequestMapping(value = "/static/Knifelist")
    @ResponseBody
    public JSONObject getKnife(String time, HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        List<KnifeList> list;
        if (time == null || time.length() == 0) {
            list = knifeListServiceImpl.getKnifeList(group.getGroupQQ(), LocalDateTime.now());
        } else {
            LocalDate localDate = LocalDate.parse(time);
            LocalDateTime startlocalDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
            localDate.plusDays(1);
            LocalDateTime endlocalDateTime = LocalDateTime.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth(), 5, 0);
            list = knifeListServiceImpl.getKnifeList(group.getGroupQQ(), startlocalDateTime, endlocalDateTime);
        }
        List<String> categories = new LinkedList<>();
        List<Integer> data = new LinkedList<>();
        for (KnifeList k : list) {
            categories.add(teamMemberServiceImpl.getName(k.getKnifeQQ()));//------------------------取名字需要优化
            data.add(k.getHurt());
        }
        ReportDataBean reportDataBean = new ReportDataBean(categories, data);
        JSONObject jsonObj = (JSONObject) JSON.toJSON(reportDataBean);
        return jsonObj;//现在就是今天的刀
    }

    //返回那天的打全刀的人
    @RequestMapping(value = "/static/normal")
    @ResponseBody
    public JSONObject gete(String time, HttpSession session) {
        PcrUnion group = (PcrUnion) session.getAttribute("group");
        List<KnifeList> lists = knifeListServiceImpl.getKnifeList(group.getGroupQQ(), LocalDateTime.now());
        long sum = 0;//总伤害
        int min = 10000, max = 0;//最小王编号和最大王编号
        if (lists != null && lists.size() > 0) {
            for (KnifeList knifeList : lists) {
                sum += knifeList.getHurt();
                if (max < knifeList.getLoop() * 10 + knifeList.getPosition()) {
                    max = knifeList.getLoop() * 10 + knifeList.getPosition();
                }
                if (min > knifeList.getLoop() * 10 + knifeList.getPosition()) {
                    min = knifeList.getLoop() * 10 + knifeList.getPosition();
                }
            }
        } else {
            min = 0;
        }
        ReportDataBean1 ReportDataBean1 = new ReportDataBean1(lists.size(), sum, ((max / 10) - (min / 10)) * 5 + max % 10 - min % 10);
        JSONObject jsonObj = (JSONObject) JSON.toJSON(ReportDataBean1);

        return jsonObj;
    }


    public class ReportDataBean1 {
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
