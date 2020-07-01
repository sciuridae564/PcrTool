package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.PcrUnion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
public interface PcrUnionService extends IService<PcrUnion> {

    PcrUnion getGroup(long groupQq);


    int deleteGroup(long groupQq);


    boolean changeGroupName(long groupQq, String new_name);

    boolean isGroupMaster(long qq, long groupQq);

    long getGroupMaster(long groupQq);

    boolean changeGroupMaster(long groupQq, long new_qq);

    int getVoidSize(long groupQq);

    int updateVoidSize(long groupQq);

    int changeUnionName(long groupQq, String name);
}
