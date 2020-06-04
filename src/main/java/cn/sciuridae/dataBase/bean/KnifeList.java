package cn.sciuridae.dataBase.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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
@ApiModel(value = "KnifeList对象", description = "")
@TableName("knifeList")
public class KnifeList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long knifeQQ;

    private Integer hurt;

    private LocalDateTime date;

    private Boolean complete;
    @Version
    private Integer version = 1;

    private Integer loop;
    private Integer position;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getKnifeQQ() {
        return knifeQQ;
    }

    public void setKnifeQQ(Long knifeQQ) {
        this.knifeQQ = knifeQQ;
    }

    public Integer getHurt() {
        return hurt;
    }

    public void setHurt(Integer hurt) {
        this.hurt = hurt;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getLoop() {
        return loop;
    }

    public void setLoop(Integer loop) {
        this.loop = loop;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "KnifeList{" +
                "id=" + id +
                ", knifeQQ=" + knifeQQ +
                ", hurt=" + hurt +
                ", date=" + date +
                ", complete=" + complete +
                ", version=" + version +
                ", loop=" + loop +
                ", position=" + position +
                '}';
    }
}
