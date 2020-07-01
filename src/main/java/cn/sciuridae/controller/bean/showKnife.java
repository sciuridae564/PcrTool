package cn.sciuridae.controller.bean;

import java.time.LocalDateTime;

public class showKnife {

    private Integer id;

    private Long knifeQQ;
    private String name;

    private Integer hurt;

    private LocalDateTime date;

    private Boolean complete;

    private Integer loop;
    private Integer position;

    public showKnife(Integer id, Long knifeQQ, String name, Integer hurt, LocalDateTime date, Boolean complete, Integer loop, Integer position) {
        this.id = id;
        this.knifeQQ = knifeQQ;
        this.name = name;
        this.hurt = hurt;
        this.date = date;
        this.complete = complete;
        this.loop = loop;
        this.position = position;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "showKnife{" +
                "id=" + id +
                ", knifeQQ=" + knifeQQ +
                ", name='" + name + '\'' +
                ", hurt=" + hurt +
                ", date=" + date +
                ", complete=" + complete +
                ", loop=" + loop +
                ", position=" + position +
                '}';
    }
}
