package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.TeamMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
@Repository
@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    @Select("select power from   teamMember  " +
            "where  groupQQ=#{groupQq} AND userQQ = #{Qq}  ")
    Boolean isAdmin(@Param("groupQq") long groupQq, @Param("Qq") long Qq);

    @Select("select groupQQ from teamMember where userQQ = #{Qq} ")
    Long group(@Param("Qq") long Qq);

    @Select("select userQQ from teamMember where groupQQ = #{groupQq} ")
    List<Long> userQQs(@Param("groupQq") long groupQq);

    @Update("update teamMember set power=true where userQQ = #{Qq} ")
    int setAdmin(@Param("Qq") long Qq);

    @Update("update teamMember set name=#{name} where userQQ = #{Qq} ")
    int setName(@Param("Qq") long Qq, @Param("name") String name);

    @Update("update teamMember set power=false where userQQ = #{Qq} ")
    int deAdmin(@Param("Qq") long Qq);

    @Update("update teamMember set token=#{token} where userQQ = #{userQQ} ")
    int updateToken(@Param("token") String token, @Param("userQQ") Long userQQ);

    @Select("select name from teamMember where userQQ = #{Qq}")
    String getName(@Param("Qq") long Qq);

    @Select("select * from teamMember where token = #{token}")
    TeamMember getTeamMember(@Param("token") String token);
}
