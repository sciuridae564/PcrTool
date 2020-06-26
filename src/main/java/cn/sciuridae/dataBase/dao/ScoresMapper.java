package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.Scores;
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
public interface ScoresMapper extends BaseMapper<Scores> {

    @Update("update Scores set iSign=false ")
    void changeTeamSum();

    @Select("select iSign from Scores where QQ=#{qq}")
    Boolean selectSign(@Param("qq") long qq);

    @Update("update Scores set iSign=true where QQ=#{qq}")
    int sign(@Param("qq") long qq);

    @Update("update Scores set #{size}=0 where QQ=#{qq}")
    int clear(@Param("qq") long qq, String size);

    @Update("update Scores set #{size}=#{mid} where QQ=#{qq}")
    int setLive(@Param("qq") long qq, String size, long mid);
}
