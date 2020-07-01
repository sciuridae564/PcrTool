package cn.sciuridae.dataBase.service;

import cn.sciuridae.dataBase.bean.Tree;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 挂树战斗中服务类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
public interface TreeService extends IService<Tree> {


    /* 获取需要清除的树表 */
    List<Tree> getNeedClearTree();

    /* 删除这个工会的挂树 返回清除的qq号*/
    List<Tree> deletTreeByGroup(long group);

    /* 获取这个工会的挂树*/
    List<Tree> getTreeByGroup(long group);

    /* 获取这个工会的在战斗的*/
    List<Tree> getFightByGroup(long group);

    /* 获取这个人的挂树 0没挂 1在战斗，2挂了 */
    int getTreeByQQ(long QQ);

    int updateTree(long QQ);


}
