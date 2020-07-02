package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.qqGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

@Service
public interface qqGroupService extends IService<qqGroup> {

    String getGroupWelcom(long group);

    boolean isGroupWelcomOn(long group);

    String setGroupWelcom(long group, String str);


}
