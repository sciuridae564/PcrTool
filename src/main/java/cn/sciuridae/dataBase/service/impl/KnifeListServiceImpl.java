package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.KnifeList;
import cn.sciuridae.dataBase.dao.KnifeListMapper;
import cn.sciuridae.dataBase.service.KnifeListService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cn.sciuridae.utils.timeUtil.getTodayNoStart;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
@Service
public class KnifeListServiceImpl extends ServiceImpl<KnifeListMapper, KnifeList> implements KnifeListService {
    @Autowired
    KnifeListMapper knifeListMapper;

    /**
     * 通过qq和开始日期和结束日期找刀数据
     *
     * @param knifeQQ
     * @param startdate 开始日期
     * @param enddate   结束日期
     * @return
     */
    public List<KnifeList> getKnifeListbyQQAndDate(long knifeQQ, LocalDateTime startdate, LocalDateTime enddate) {
        QueryWrapper<KnifeList> wrapper = new QueryWrapper<>();
        wrapper.eq("knifeQQ", knifeQQ);
        wrapper.ge("date", startdate);
        wrapper.lt("date", enddate);
        return knifeListMapper.selectList(wrapper);
    }

    /**
     * 传入的日期那一天的刀
     *
     * @param knifeQQ
     * @param date
     * @return
     */
    public List<KnifeList> getKnifeListbyQQAndDate(long knifeQQ, LocalDateTime date) {
        LocalDateTime[] dates = getTodayNoStart(date, false);
        QueryWrapper<KnifeList> wrapper = new QueryWrapper<>();
        wrapper.eq("knifeQQ", knifeQQ);
        wrapper.ge("date", dates[0]);
        wrapper.lt("date", dates[1]);
        return knifeListMapper.selectList(wrapper);
    }


    public void addKnifeList(long QQ, int hurt, LocalDateTime date) {
        KnifeList knifeList = new KnifeList();
        knifeList.setKnifeQQ(QQ);
        knifeList.setDate(date);
        knifeList.setHurt(hurt);
        knifeListMapper.insert(knifeList);
    }


    @Override
    public List<KnifeList> getKnifeList(long group) {
        return knifeListMapper.getKnife(group);
    }

    @Override
    public List<KnifeList> getKnifeList(long group, LocalDateTime date) {
        LocalDateTime[] dates = getTodayNoStart(date, false);
        return knifeListMapper.getKnifeBygroupQQ(group, dates[0], dates[1]);
    }

    @Override
    public List<KnifeList> getKnifeList(long group, LocalDateTime startdate, LocalDateTime endate) {

        return knifeListMapper.getKnifeBygroupQQ(group, startdate, endate);
    }

    @Override
    public List<KnifeList> getKnife(long Qq, LocalDateTime date) {
        LocalDateTime[] dates = getTodayNoStart(date, false);
        QueryWrapper<KnifeList> queryWrapper=new QueryWrapper<>();
        queryWrapper.between("date",dates[0],dates[1]);
        return knifeListMapper.selectList(queryWrapper);
    }

    @Override
    public List<KnifeList> getKnife(long Qq) {
        return knifeListMapper.getKnifeByUserQQ(Qq);
    }

    @Override
    public List<KnifeList> getKnife(long Qq, LocalDateTime startdate, LocalDateTime endate) {
        QueryWrapper<KnifeList> queryWrapper=new QueryWrapper<>();
        queryWrapper.between("date",startdate,endate);

        return knifeListMapper.selectList(queryWrapper);
    }

    @Override
    public int getKnifeNum(long Qq, LocalDateTime date, boolean isNoReward) {
        LocalDateTime[] dates = getTodayNoStart(date, false);
        if (isNoReward) {
            return knifeListMapper.getKnifeNumByUserQQNoReward(Qq, dates[0], dates[1]);
        } else {
            return knifeListMapper.getKnifeNumByUserQQ(Qq, dates[0], dates[1]);
        }

    }


    @Override
    public int getKnifeNum(long Qq, LocalDateTime startdate, LocalDateTime endate, boolean isNoReward) {
        if (isNoReward) {
            return knifeListMapper.getKnifeNumByUserQQNoReward(Qq, startdate, endate);
        } else {
            return knifeListMapper.getKnifeNumByUserQQ(Qq, startdate, endate);
        }

    }

    @Override
    public KnifeList getKnifeLast(long Qq, LocalDateTime startdate, LocalDateTime endate) {
        return null;
    }

    @Override
    public List<KnifeList> getKnifeSumQq(long Qq, LocalDateTime startdate, LocalDateTime endate) {
        return knifeListMapper.getSumKnifeByQQ(Qq, startdate, endate);
    }

    @Override
    public List<KnifeList> getKnifeSumByGroup(long groupQq, LocalDateTime startdate, LocalDateTime endate) {

        return knifeListMapper.getSumKnifeBygroupQQ(groupQq, startdate, endate);
    }

    @Override
    public KnifeList getTopKnife(long groupQq) {
        LocalDateTime[] dates = getTodayNoStart(LocalDateTime.now(), false);
        List<KnifeList> knives = getKnifeSumByGroup(groupQq, dates[0], dates[1]);
        KnifeList Topknife = knives.get(0);
        for (int i = 1; i < knives.size(); i++) {
            if (knives.get(i).getHurt() > Topknife.getHurt()) { //找最高伤害的
                Topknife = knives.get(i);
            }
        }
        return Topknife;
    }


}
