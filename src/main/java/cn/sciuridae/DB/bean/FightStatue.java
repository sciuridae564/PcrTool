package cn.sciuridae.DB.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FightStatue {
    private int loop;
    private int serial;
    private int Remnant;
    private String startTime;
    @JsonProperty(value="endTime")//不知道为啥这玩意老是丢失
    private String endTime;
    private int dem;//boss血量百分比


    public FightStatue(int loop, int serial, int remnant) {
        this.loop = loop;
        this.serial = serial;
        Remnant = remnant;
    }

    public FightStatue(int loop, int serial, int remnant, String startTime) {
        this.loop = loop;
        this.serial = serial;
        Remnant = remnant;
        this.startTime = startTime;
    }

    public FightStatue(int loop, int serial, int remnant, String startTime,String endTime) {
        this.loop = loop;
        this.serial = serial;
        Remnant = remnant;
        this.startTime = startTime;
        this.dem = dem;
        this.endTime=endTime;
    }

    public int getDem() {
        return dem;
    }

    public void setDem(int dem) {
        this.dem = dem;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public int getRemnant() {
        return Remnant;
    }

    public void setRemnant(int remnant) {
        Remnant = remnant;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "FightStatue{" +
                "loop=" + loop +
                ", serial=" + serial +
                ", Remnant=" + Remnant +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", dem=" + dem +
                '}';
    }
}
