package cn.sciuridae.utils.bean;

public class Gashapon {
    String data;
    boolean ban;

    public Gashapon() {
    }

    public Gashapon(String data, boolean ban) {
        this.data = data;
        this.ban = ban;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }
}
