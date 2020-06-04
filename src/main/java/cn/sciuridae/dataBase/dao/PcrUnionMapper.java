package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.PcrUnion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

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
public interface PcrUnionMapper extends BaseMapper<PcrUnion> {

    @Select("select teamSum from pcrUnion where groupQQ=#{groupQq} ")
    int getTeamSum(@Param("groupQq") long groupQq);

    @Update("update pcrUnion set teamSum=#{teamSum} where groupQQ=#{groupQq} ")
    int changeTeamSum(@Param("groupQq") long groupQq, @Param("teamSum") int teamSum);

    @Update("update pcrUnion set groupName=#{name} where groupQQ=#{groupQq} ")
    int changeTeamName(@Param("groupQq") long groupQq, String name);

}
