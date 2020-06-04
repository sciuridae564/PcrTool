package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.KnifeList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
public interface KnifeListMapper extends BaseMapper<KnifeList> {

    @Select("select knifeList.* from knifeList left join teamMember on teamMember.userQQ=knifeList.knifeQQ " +
            "where teamMember.groupQQ=#{groupQq} AND date between #{startdate} and #{endate}")
    List<KnifeList> getKnifeBygroupQQ(@Param("groupQq") long groupQq, @Param("startdate") LocalDateTime startdate, @Param("endate") LocalDateTime endate);

    @Select("select knifeList.* from knifeList left join teamMember on teamMember.userQQ=knifeList.knifeQQ " +
            "where teamMember.groupQQ=#{groupQq}")
    List<KnifeList> getKnife(@Param("groupQq") long groupQq);

    @Select("select * from knifeList " +
            " where knifeQQ=#{knifeQQ} AND date between #{startdate} and #{endate} order by date")
    List<KnifeList> getKnifeByUserQQ(@Param("knifeQQ") long knifeQQ);

    @Select("select * from knifeList " +
            " where knifeQQ=#{knifeQQ} AND date =#{localDate} order by date")
    List<KnifeList> getKnifeByUserQQ(@Param("knifeQQ") long knifeQQ, @Param("localDate") LocalDate localDate);

    @Select("select * from knifeList " +
            " where knifeQQ=#{knifeQQ} AND date between #{startdate} and #{endate} order by date")
    List<KnifeList> getKnifeByUserQQ(@Param("knifeQQ") long knifeQQ, @Param("startdate") LocalDateTime startdate, @Param("endate") LocalDateTime endate);


    @Select("select count(id) from knifeList " +
            " where knifeQQ=#{knifeQQ} AND date between #{startdate} and #{endate}")
    int getKnifeNumByUserQQ(@Param("knifeQQ") long knifeQQ, @Param("startdate") LocalDateTime startdate, @Param("endate") LocalDateTime endate);

    @Select("select count(id) from knifeList " +
            " where complete=true AND knifeQQ=#{knifeQQ} AND date between #{startdate} and #{endate}")
    int getKnifeNumByUserQQNoReward(@Param("knifeQQ") long knifeQQ, @Param("startdate") LocalDateTime startdate, @Param("endate") LocalDateTime endate);

}
