package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.qqGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface qqGroupMapper extends BaseMapper<qqGroup> {

    @Select("select welcome from qqGroup where group_number=#{group}")
    String getGroupWelcom(long group);

    @Select("select welcome_tri from qqGroup where group_number=#{group}")
    boolean isGroupWelcomOn(long group);

    @Update("update qqGroup set welcome=#{str} where group_number=#{group}")
    String setGroupWelcom(long group, String str);

}
