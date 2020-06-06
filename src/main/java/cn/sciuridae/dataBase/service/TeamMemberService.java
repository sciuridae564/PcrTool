package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.TeamMember;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 工会成员服务类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
public interface TeamMemberService extends IService<TeamMember> {

    /* 获取工会成员列表通过工会qq */
    List<TeamMember> getTeamMemberByGroup(long group);

    /* 获取工会成员qq列表通过工会qq */
    List<Long> getTeamMemberQQByGroup(long group);

    /*删了整个工会的人员*/
    int deleteTeamMemberByGroup(long group);

    //获取这个qq的工会成员
    TeamMember getTeamMemberByQQ(long QQ);

    TeamMember getTeamMemberBytoken(String token);

    boolean isAdmin(long QQ, long GroupQq);

    Long getGroupByQQ(long QQ);

    long setAdmin(long QQ);

    long deAdmin(long QQ);

    String getName(long QQ);

    Integer setName(long QQ, String name);

    String getToken(long QQ);

    Integer getTokenNum(String token);

    Integer updateToken(Long qq, String token);
}
