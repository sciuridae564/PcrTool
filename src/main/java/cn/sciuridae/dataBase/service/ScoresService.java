package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.Scores;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 积分服务类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
public interface ScoresService extends IService<Scores> {

    void clearSign();

    Boolean selectSign(long qq);

    void sign(long qq);

    //设置一个直播记录，返回设置的槽位，满了返回-1
    int setLive(long qq, String live);

    //清除指定位置的直播记录
    int clearLive(long qq, String size);

    //获取数据库中开启直播监听的人的数据
    List<Scores> getLive();

    int updateLiveOn(long qq, boolean on);
}
