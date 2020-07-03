package cn.sciuridae.dataBase.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("qqGroup")
public class qqGroup {

    private static final long serialVersionUID = 1L;

    @TableId(value = "group_number")
    private Long group_number;

    private String welcome;

    private Boolean welcome_tri;

    public Long getGroup_number() {
        return group_number;
    }

    public void setGroup_number(Long group_number) {
        this.group_number = group_number;
    }

    public String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        this.welcome = welcome;
    }

    public Boolean getWelcome_tri() {
        return welcome_tri;
    }

    public void setWelcome_tri(Boolean welcome_tri) {
        this.welcome_tri = welcome_tri;
    }

    @Override
    public String toString() {
        return "qqGroup{" +
                "group_number=" + group_number +
                ", welcome='" + welcome + '\'' +
                ", welcome_tri=" + welcome_tri +
                '}';
    }
}
