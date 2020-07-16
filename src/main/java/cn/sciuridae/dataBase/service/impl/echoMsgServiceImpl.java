package cn.sciuridae.dataBase.service.impl;

import cn.sciuridae.dataBase.bean.echoMsg;
import cn.sciuridae.dataBase.dao.echoMsgMapper;
import cn.sciuridae.dataBase.service.echoMsgService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class echoMsgServiceImpl  extends ServiceImpl<echoMsgMapper, echoMsg> implements echoMsgService {

    @Autowired
    echoMsgMapper echoMsgMapper;


    @Override
    public List<echoMsg> search(String msg,long group) {
        return echoMsgMapper.search(msg,group);
    }



    @Override
    public List<echoMsg> be_search(long group,long qq, LocalDateTime time) {
        QueryWrapper<echoMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.gt("time",time);
        queryWrapper.eq("group_number",group);
        queryWrapper.eq("qq_number",qq);
        queryWrapper.orderByAsc("time");
        return echoMsgMapper.selectList(queryWrapper);
    }

    @Override
    public List<echoMsg> af_search(long group,long qq, LocalDateTime time) {
        QueryWrapper<echoMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.lt("time",time);
        queryWrapper.eq("group_number",group);
        queryWrapper.eq("qq_number",qq);
        queryWrapper.orderByDesc("time");
        return echoMsgMapper.selectList(queryWrapper);
    }

    @Override
    public int delect(String msg, long group) {
        QueryWrapper<echoMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("group_number",group);
        queryWrapper.eq("msg",msg);
        return echoMsgMapper.delete(queryWrapper);
    }

    @Override
    public int clear() {
        LocalDateTime localDateTime=LocalDateTime.now();
        localDateTime=localDateTime.plusMinutes(30);
        QueryWrapper<echoMsg> queryWrapper=new QueryWrapper<>();
        queryWrapper.ge("time",localDateTime);
        return echoMsgMapper.delete(queryWrapper);
    }
}
