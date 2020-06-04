package cn.sciuridae.dataBase.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author sciuridae
 * @since 2020-06-02
 */
@ApiModel(value = "Scores对象", description = "")
@TableName("Scores")
public class Scores implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "QQ")
    private Long QQ;

    private Boolean iSign;

    private Integer score;


    public Long getQQ() {
        return QQ;
    }

    public void setQQ(Long QQ) {
        this.QQ = QQ;
    }

    public Boolean getiSign() {
        return iSign;
    }

    public void setiSign(Boolean iSign) {
        this.iSign = iSign;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Scores{" +
                "QQ=" + QQ +
                ", iSign=" + iSign +
                ", score=" + score +
                "}";
    }
}
