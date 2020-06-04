package cn.sciuridae.dataBase.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value = "PcrUnion对象", description = "")
@TableName("pcrUnion")
public class PcrUnion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private long groupQQ;

    private String groupName;

    private long groupMasterQQ;

    private LocalDateTime createDate;

    private Integer teamSum;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getGroupQQ() {
        return groupQQ;
    }

    public void setGroupQQ(long groupQQ) {
        this.groupQQ = groupQQ;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getGroupMasterQQ() {
        return groupMasterQQ;
    }

    public void setGroupMasterQQ(long groupMasterQQ) {
        this.groupMasterQQ = groupMasterQQ;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Integer getTeamSum() {
        return teamSum;
    }

    public void setTeamSum(Integer teamSum) {
        this.teamSum = teamSum;
    }

    @Override
    public String toString() {
        return "PcrUnion{" +
                "id=" + id +
                ", groupQQ=" + groupQQ +
                ", groupName=" + groupName +
                ", groupMasterQQ=" + groupMasterQQ +
                ", createDate=" + createDate +
                ", teamSum=" + teamSum +
                "}";
    }
}
