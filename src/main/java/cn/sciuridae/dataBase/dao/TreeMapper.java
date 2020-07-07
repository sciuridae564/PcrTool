package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.Tree;
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
public interface TreeMapper extends BaseMapper<Tree> {

    @Select("SELECT * FROM tree WHERE groupQQ = #{group} AND isTree=true")
    List<Tree> getTreeByGroup(@Param("group") long group);

    @Select("SELECT * FROM tree WHERE groupQQ = #{group} AND isTree=false")
    List<Tree> getFightByGroup(@Param("group") long group);

    @Update("update tree set isTree=true WHERE teamQQ = #{teamQQ}")
    int updateTree(@Param("teamQQ") long teamQQ);

}
