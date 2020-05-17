package cn.sciuridae.DB.bean;

public class teamMember {
    private String userQQ;//组员qq
    private boolean power;//权限 1为管理员
    private Integer id;//工会id 主键
    private String name;//组员游戏昵称


    public teamMember(String userQQ, boolean power, Integer id, String name) {
        this.userQQ = userQQ;
        this.power = power;
        this.id = id;
        this.name = name;
    }

    public String getUserQQ() {
        return userQQ;
    }

    public void setUserQQ(String userQQ) {
        this.userQQ = userQQ;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "teamMember{" +
                "userQQ='" + userQQ + '\'' +
                ", power=" + power +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
