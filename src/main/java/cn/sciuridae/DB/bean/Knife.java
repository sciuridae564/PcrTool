package cn.sciuridae.DB.bean;

//其实是已经出完刀的刀表

public class Knife {
    private int id;//出刀序号
    private String knifeQQ;//出刀人qq
    private String name;//出刀人名字
    private int no;//出刀几王,11 是一轮一王，23是二轮3王
    private int hurt;//伤害
    private String date;//出刀时间，即使是第二天3点也算第一天
    private boolean complete;

    public Knife(String knifeQQ, int no, int hurt, String date, boolean complete, int id) {
        this.id = id;
        this.knifeQQ = knifeQQ;
        this.no = no;
        this.hurt = hurt;
        this.date = date;
        this.complete = complete;
    }

    public Knife(String knifeQQ, int no, int hurt, boolean complete) {
        this.knifeQQ = knifeQQ;
        this.no = no;
        this.hurt = hurt;
        this.complete = complete;
    }

    public Knife(String knifeQQ, int no, int hurt, String date, boolean complete) {
        this.knifeQQ = knifeQQ;
        this.no = no;
        this.hurt = hurt;
        this.date = date;
        this.complete = complete;
    }
    public Knife(int rowid,String knifeQQ, int no, int hurt, String date, boolean complete) {
        this.id=rowid;
        this.knifeQQ = knifeQQ;
        this.no = no;
        this.hurt = hurt;
        this.date = date;
        this.complete = complete;
    }
    public Knife(String knifeQQ, int no, int hurt, String date) {
        this.knifeQQ = knifeQQ;
        this.no = no;
        this.hurt = hurt;
        this.date = date;
    }

    public Knife(int id, String knifeQQ, String name, int no, int hurt, String date, boolean complete) {
        this.id = id;
        this.knifeQQ = knifeQQ;
        this.name = name;
        this.no = no;
        this.hurt = hurt;
        this.date = date;
        this.complete = complete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Knife{" +
                "id=" + id +
                ", knifeQQ='" + knifeQQ + '\'' +
                ", name='" + name + '\'' +
                ", no=" + no +
                ", hurt=" + hurt +
                ", date='" + date + '\'' +
                ", complete=" + complete +
                '}';
    }
}
