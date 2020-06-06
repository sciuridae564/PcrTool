package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.Progress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
public interface ProgressMapper extends BaseMapper<Progress> {

    @Select("select Remnant from progress where teamQQ=#{groupQq} ")
    Integer getFight(@Param("groupQq") long groupQq);

    @Select("select * from progress where teamQQ=#{groupQq} ")
    Progress getProgress(@Param("groupQq") long groupQq);

    @Select("select teamQQ from progress where endTime < #{endTime} ")
    List<Long> getEnd(@Param("endTime") LocalDateTime endTime);

    @Update("update progress set loop=#{loop},serial=#{serial},Remnant=#{Remnant},endTime=#{endTime},startTime=#{startTime} where teamQQ < #{teamQQ} ")
    int updataProgress(@Param("teamQQ") long teamQQ, @Param("loop") int loop, @Param("serial") int serial, @Param("Remnant") long Remnant, @Param("endTime") LocalDateTime endTime, @Param("startTime") LocalDateTime startTime);

}
