package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.TeamMember;
import cn.sciuridae.dataBase.dao.TeamMemberMapper;
import cn.sciuridae.dataBase.service.TeamMemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
@Service
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements TeamMemberService {
    @Autowired
    TeamMemberMapper teamMemberMapper;


    @Override
    public List<TeamMember> getTeamMemberByGroup(long group) {
        QueryWrapper<TeamMember> wrapper = new QueryWrapper<>();
        wrapper.eq("groupQQ", group);
        List<TeamMember> list = teamMemberMapper.selectList(wrapper);
        return list;
    }

    @Override
    public List<Long> getTeamMemberQQByGroup(long group) {
        return teamMemberMapper.userQQs(group);
    }


    @Override
    public int deleteTeamMemberByGroup(long group) {
        QueryWrapper<TeamMember> wrapper = new QueryWrapper<>();
        wrapper.eq("groupQQ", group);
        return teamMemberMapper.delete(wrapper);
    }

    @Override
    public TeamMember getTeamMemberByQQ(long QQ) {
        return teamMemberMapper.selectById(QQ);
    }

    @Override
    public boolean isAdmin(long QQ, long GroupQq) {
        return teamMemberMapper.isAdmin(GroupQq, QQ);
    }

    @Override
    public Long getGroupByQQ(long QQ) {
        return teamMemberMapper.group(QQ);
    }

    @Override
    public long setAdmin(long QQ) {
        return teamMemberMapper.setAdmin(QQ);
    }

    @Override
    public long deAdmin(long QQ) {
        return teamMemberMapper.deAdmin(QQ);
    }

    @Override
    public String getName(long QQ) {
        return teamMemberMapper.getName(QQ);
    }

    @Override
    public Integer setName(long QQ, String name) {

        return teamMemberMapper.setName(QQ, name);
    }

    @Override
    public String getToken(long QQ) {
        return teamMemberMapper.getToken(QQ);
    }

    @Override
    public Integer getTokenNum(String token) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("token", token);
        return teamMemberMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer updateToken(Long qq, String token) {
        return teamMemberMapper.updateToken(token, qq);
    }
}
