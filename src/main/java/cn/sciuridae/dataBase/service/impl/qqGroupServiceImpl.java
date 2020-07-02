package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.qqGroup;
import cn.sciuridae.dataBase.dao.qqGroupMapper;
import cn.sciuridae.dataBase.service.qqGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class qqGroupServiceImpl extends ServiceImpl<qqGroupMapper, qqGroup> implements qqGroupService {
    @Autowired
    qqGroupMapper qqGroupMapper;


    @Override
    public String getGroupWelcom(long group) {
        return qqGroupMapper.getGroupWelcom(group);
    }

    @Override
    public boolean isGroupWelcomOn(long group) {
        return qqGroupMapper.isGroupWelcomOn(group);
    }

    @Override
    public String setGroupWelcom(long group, String str) {
        return qqGroupMapper.setGroupWelcom(group, str);
    }
}
