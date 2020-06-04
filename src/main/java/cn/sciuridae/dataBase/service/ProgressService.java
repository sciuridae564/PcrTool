package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.Progress;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
public interface ProgressService extends IService<Progress> {

    Integer isFight(long group);

    Progress getProgress(long Groupqq);

    int endFight(long Groupqq);
}
