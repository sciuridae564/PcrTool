package cn.sciuridae.dataBase.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("echoMsg")
public class echoMsg implements Serializable {

    @TableId(value = "flag", type = IdType.AUTO)
    private Integer flag;

    private Long group_number;
    private Long qq_number;
    private String msg;
    private LocalDateTime time;
    private boolean echoOk=false;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Long getGroup_number() {
        return group_number;
    }

    public void setGroup_number(Long group_number) {
        this.group_number = group_number;
    }

    public Long getQq_number() {
        return qq_number;
    }

    public void setQq_number(Long qq_number) {
        this.qq_number = qq_number;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public boolean isEchoOk() {
        return echoOk;
    }

    public void setEchoOk(boolean echoOk) {
        this.echoOk = echoOk;
    }

    @Override
    public String toString() {
        return "echoMsg{" +
                "flag=" + flag +
                ", group_number=" + group_number +
                ", qq_number=" + qq_number +
                ", msg='" + msg + '\'' +
                ", time=" + time +
                ", echoOk=" + echoOk +
                '}';
    }
}
