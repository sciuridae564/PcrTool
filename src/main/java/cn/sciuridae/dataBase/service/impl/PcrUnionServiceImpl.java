package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.PcrUnion;
import cn.sciuridae.dataBase.dao.PcrUnionMapper;
import cn.sciuridae.dataBase.service.PcrUnionService;
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
public class PcrUnionServiceImpl extends ServiceImpl<PcrUnionMapper, PcrUnion> implements PcrUnionService {
    @Autowired
    PcrUnionMapper pcrUnionMapper;


    @Override
    public PcrUnion getGroup(long groupQq) {
        return pcrUnionMapper.getTeam(groupQq);
    }


    @Override
    public int deleteGroup(long groupQq) {
        QueryWrapper<PcrUnion> wrapper = new QueryWrapper<>();
        wrapper.eq("groupQQ", groupQq);
        return pcrUnionMapper.delete(wrapper);
    }

    @Override
    public boolean changeGroupName(long groupQq, String new_name) {
        QueryWrapper<PcrUnion> wrapper = new QueryWrapper<>();
        wrapper.eq("groupQQ", groupQq);
        PcrUnion pcrUnion = pcrUnionMapper.selectOne(wrapper);
        pcrUnion.setGroupName(new_name);
        pcrUnionMapper.updateById(pcrUnion);
        return false;
    }

    @Override
    public boolean isGroupMaster(long qq, long groupQq) {
        QueryWrapper<PcrUnion> wrapper = new QueryWrapper<>();
        wrapper.eq("groupQQ", groupQq);
        wrapper.eq("groupMasterQQ", qq);
        PcrUnion pcrUnion = pcrUnionMapper.selectOne(wrapper);
        return pcrUnion != null;
    }

    @Override
    public long getGroupMaster(long groupQq) throws NullPointerException {
        QueryWrapper<PcrUnion> wrapper = new QueryWrapper<>();
        wrapper.eq("groupQQ", groupQq);
        return pcrUnionMapper.selectOne(wrapper).getGroupMasterQQ();
    }

    @Override
    public boolean changeGroupMaster(long groupQq, long new_qq) {
        QueryWrapper<PcrUnion> wrapper = new QueryWrapper<>();
        wrapper.eq("groupQQ", groupQq);
        PcrUnion pcrUnion = new PcrUnion();
        pcrUnion.setGroupQQ(groupQq);
        pcrUnion.setGroupMasterQQ(new_qq);
        return pcrUnionMapper.update(pcrUnion, wrapper) == 1;
    }

    @Override
    public int getVoidSize(long groupQq) {
        return pcrUnionMapper.getTeamSum(groupQq);
    }

    @Override
    public int changeVoidSize(long groupQq, int teamSum) {
        return pcrUnionMapper.changeTeamSum(groupQq, teamSum);
    }


    @Override
    public int changeUnionName(long groupQq, String name) {
        return pcrUnionMapper.changeTeamName(groupQq, name);
    }

}
