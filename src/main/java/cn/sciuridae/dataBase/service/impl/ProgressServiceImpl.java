package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.Progress;
import cn.sciuridae.dataBase.dao.ProgressMapper;
import cn.sciuridae.dataBase.service.ProgressService;
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
public class ProgressServiceImpl extends ServiceImpl<ProgressMapper, Progress> implements ProgressService {
    @Autowired
    ProgressMapper progressMapper;


    @Override
    public Integer isFight(long group) {
        return progressMapper.getFight(group);
    }

    @Override
    public Progress getProgress(long Groupqq) {
        return progressMapper.getProgress(Groupqq);
    }

    @Override
    public int endFight(long Groupqq) {
        QueryWrapper<Progress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamQQ", Groupqq);
        return progressMapper.delete(queryWrapper);
    }
}
