package cn.sciuridae.DB.bean;

public class FightStatue {
    private int loop;
    private int serial;
    private int Remnant;


    public FightStatue(int loop, int serial, int remnant) {
        this.loop = loop;
        this.serial = serial;
        Remnant = remnant;
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
