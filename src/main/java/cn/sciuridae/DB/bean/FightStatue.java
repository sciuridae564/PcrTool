package cn.sciuridae.DB.bean;

public class FightStatue {
    private int loop;
    private int serial;
    private int Remnant;
    private String startTime;


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
}
