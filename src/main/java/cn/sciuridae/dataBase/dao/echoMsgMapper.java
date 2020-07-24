package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.echoMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface echoMsgMapper extends BaseMapper<echoMsg> {

}
