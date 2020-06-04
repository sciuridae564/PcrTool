package cn.sciuridae.dataBase.bean;

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
@ApiModel(value = "Sqlite_sequence对象", description = "")
public class Sqlite_sequence implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String seq;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return "Sqlite_sequence{" +
                "name=" + name +
                ", seq=" + seq +
                "}";
    }
}
