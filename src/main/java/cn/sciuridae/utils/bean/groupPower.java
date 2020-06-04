package cn.sciuridae.utils.bean;

public class groupPower {
    private boolean on = true;//总开关
    private boolean eggon = true;//扭蛋开关
    private boolean buton = true;//买药小助手提示
    private boolean horse = true;//赛马开关

    public boolean isOn() {
        return on;
    }

    public groupPower setOn(boolean on) {
        this.on = on;
        return this;
    }

    public boolean isEggon() {
        return eggon;
    }

    public groupPower setEggon(boolean eggon) {
        this.eggon = eggon;
        return this;
    }

    public boolean isButon() {
        return buton;
    }

    public groupPower setButon(boolean buton) {
        this.buton = buton;
        return this;
    }

    public boolean isHorse() {
        return horse;
    }

    public groupPower setHorse(boolean horse) {
        this.horse = horse;
        return this;
    }

    @Override
    public String toString() {
        return "groupPower{" +
                "on=" + on +
                ", eggon=" + eggon +
                ", buton=" + buton +
                ", horse=" + horse +
                '}';
    }
}
