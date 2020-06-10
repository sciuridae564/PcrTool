package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.Scores;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
