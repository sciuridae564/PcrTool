package cn.sciuridae.dataBase.dao;

import cn.sciuridae.dataBase.bean.Sqlite_sequence;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
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
public interface Sqlite_sequenceMapper extends BaseMapper<Sqlite_sequence> {

}
