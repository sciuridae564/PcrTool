package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.dao.ScoresMapper;
import cn.sciuridae.dataBase.service.ScoresService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
@Service
public class ScoresServiceImpl extends ServiceImpl<ScoresMapper, Scores> implements ScoresService {
    @Autowired
    ScoresMapper scoresMapper;

    public void clearSign() {
        scoresMapper.changeTeamSum();
    }

    @Override
    public Boolean selectSign(long qq) {
        return scoresMapper.selectSign(qq);
    }

    @Override
    public void sign(long qq) {
        scoresMapper.sign(qq);
    }

    @Override
    public int setLive(long qq, String live) {
        QueryWrapper<Scores> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("QQ", qq);
        Scores scores = scoresMapper.selectOne(queryWrapper);
        if (scores == null) {
            scores = new Scores();
            scores.setQQ(qq);
            scores.setScore(1);
            scores.setiSign(false);
            scoresMapper.insert(scores);
        }
        if (scores.getLive1() == 0) {
            scores.setLive1(Integer.valueOf(live));
            scoresMapper.updateById(scores);
            return 1;
        } else if (scores.getLive2() == 0) {
            scores.setLive2(Integer.valueOf(live));
            scoresMapper.updateById(scores);
            return 2;
        } else if (scores.getLive3() == 0) {
            scores.setLive3(Integer.valueOf(live));
            scoresMapper.updateById(scores);
            return 3;
        } else {
            return -1;
        }

    }

    @Override
    public int clearLive(long qq, String size) {
        return scoresMapper.clear(qq, size);
    }



}
