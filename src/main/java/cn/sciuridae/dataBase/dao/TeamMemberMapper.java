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
    boolean isAdmin(@Param("groupQq") long groupQq, @Param("Qq") long Qq);

    @Select("select groupQQ from teamMember where userQQ = #{Qq} ")
    long group(@Param("Qq") long Qq);

    @Select("select userQQ from teamMember where groupQQ = #{groupQq} ")
    List<Long> userQQs(@Param("groupQq") long groupQq);

    @Select("select token from teamMember where userQQ = #{Qq} ")
    String getToken(@Param("Qq") long Qq);

    @Update("update teamMember set power=true where userQQ = #{Qq} ")
    int setAdmin(@Param("Qq") long Qq);

    @Update("update teamMember set power=false where userQQ = #{Qq} ")
    int deAdmin(@Param("Qq") long Qq);

    @Update("select name from teamMember where userQQ = #{Qq}")
    String getName(@Param("Qq") long Qq);
}
