package cn.sciuridae.bean;

//其实是已经出完刀的刀表
public class Knife {
    private String knifeQQ;//出刀人qq
    private int no;//出刀几王,11 是一轮一王，23是二轮3王
    private int hurt;//伤害
    private String date;//出刀时间，即使是第二天3点也算第一天

    public Knife(String knifeQQ, int no, int hurt, String date) {
        this.knifeQQ = knifeQQ;
        this.no = no;
        this.hurt = hurt;
        this.date = date;
    }

    public String getKnifeQQ() {
        return knifeQQ;
    }

    public void setKnifeQQ(String knifeQQ) {
        this.knifeQQ = knifeQQ;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getHurt() {
        return hurt;
    }

    public void setHurt(int hurt) {
        this.hurt = hurt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Knife{" +
                "knifeQQ='" + knifeQQ + '\'' +
                ", no=" + no +
                ", hurt=" + hurt +
                ", date='" + date + '\'' +
                '}';
    }
}
