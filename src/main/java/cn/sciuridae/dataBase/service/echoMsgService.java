package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.echoMsg;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface echoMsgService extends IService<echoMsg> {

    List<echoMsg> search(String msg,long group);
    List<echoMsg> be_search(long group,long qq, LocalDateTime time);
    List<echoMsg> af_search(long group,long qq, LocalDateTime time);
    int delect(String msg,long group);
    int clear();
}
