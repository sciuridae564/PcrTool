package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.Version;
import cn.sciuridae.dataBase.dao.VersionMapper;
import cn.sciuridae.dataBase.service.VersionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
@Service
public class VersionServiceImpl extends ServiceImpl<VersionMapper, Version> implements VersionService {

    @Autowired
    VersionMapper versionMapper;

    public int getDataBaseVersion() {
        return versionMapper.selectList(null).get(0).getVersion();
    }
}
