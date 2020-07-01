package cn.sciuridae.dataBase.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */

@TableName("tree")
public class Tree implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("teamQQ")
    private Long teamQQ;
    private String name;

    private LocalDateTime date;

    private Boolean isTree;

    private Long groupQQ;


    public Long getTeamQQ() {
        return teamQQ;
    }

    public void setTeamQQ(Long teamQQ) {
        this.teamQQ = teamQQ;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Boolean getTree() {
        return isTree;
    }

    public void setTree(Boolean isTree) {
        this.isTree = isTree;
    }

    public Long getGroupQQ() {
        return groupQQ;
    }

    public void setGroupQQ(Long groupQQ) {
        this.groupQQ = groupQQ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "teamQQ=" + teamQQ +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", isTree=" + isTree +
                ", groupQQ=" + groupQQ +
                '}';
    }
}
