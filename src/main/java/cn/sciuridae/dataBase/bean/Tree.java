package cn.sciuridae.dataBase.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;

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
@ApiModel(value = "Tree对象", description = "")
@TableName("tree")
public class Tree implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long teamQQ;

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

    @Override
    public String toString() {
        return "Tree{" +
                "teamQQ=" + teamQQ +
                ", date=" + date +
                ", isTree=" + isTree +
                ", groupQQ=" + groupQQ +
                "}";
    }
}
