package cn.sciuridae.dataBase.bean;

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
@ApiModel(value = "Version对象", description = "")
@TableName("version")
public class Version implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer version;


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Version{" +
                "version=" + version +
                "}";
    }
}
