package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.Scores;
import cn.sciuridae.dataBase.dao.ScoresMapper;
import cn.sciuridae.dataBase.service.ScoresService;
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

}
