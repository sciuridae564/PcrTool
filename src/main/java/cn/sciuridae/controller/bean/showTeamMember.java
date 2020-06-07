package cn.sciuridae.controller.bean;

public class showTeamMember {
    private long userQQ;
    private String name;
    private String power;

    public showTeamMember() {
    }

    public showTeamMember(long userQQ, String name, String power) {
        this.userQQ = userQQ;
        this.name = name;
        this.power = power;
    }

    public long getUserQQ() {
        return userQQ;
    }

    public void setUserQQ(long userQQ) {
        this.userQQ = userQQ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "showTeamMember{" +
                "userQQ=" + userQQ +
                ", name='" + name + '\'' +
                ", power=" + power +
                '}';
    }
}
