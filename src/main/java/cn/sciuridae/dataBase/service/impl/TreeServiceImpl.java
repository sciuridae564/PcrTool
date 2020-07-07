package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.Tree;
import cn.sciuridae.dataBase.dao.TreeMapper;
import cn.sciuridae.dataBase.service.TreeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cn.sciuridae.utils.timeUtil.getYesterday;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
@Service
public class TreeServiceImpl extends ServiceImpl<TreeMapper, Tree> implements TreeService {
    @Autowired
    TreeMapper mapper;


    /**
     * 默认以今天为标志
     *
     * @return
     */
    @Override
    public List<Tree> getNeedClearTree() {
        QueryWrapper<Tree> wrapper = new QueryWrapper<>();
        wrapper.lt("date", getYesterday(LocalDateTime.now()));
        List<Tree> list = mapper.selectList(wrapper);//先把这些查到
        mapper.delete(wrapper);//删除所有比今天5点还要早的树信息，如果还没到5点则删除昨天五点的
        return list;
    }

    @Override
    public List<Tree> deletTreeByGroup(long group) {
        QueryWrapper<Tree> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("groupQQ",group);
        List<Tree> list =mapper.selectList(queryWrapper);
        mapper.delete(queryWrapper);
        return list;
    }

    @Override
    public List<Tree> getTreeByGroup(long group) {
        return mapper.getTreeByGroup(group);
    }

    @Override
    public List<Tree> getFightByGroup(long group) {
        return mapper.getFightByGroup(group);
    }

    @Override
    public int getTreeByQQ(long QQ) {
        Tree tree = mapper.selectById(QQ);
        if (tree == null) {
            return 0;
        } else if (tree.getTree()) {
            return 2;
        } else {
            return 1;
        }
    }

    public int updateTree(long QQ) {
        return mapper.updateTree(QQ);
    }


}
