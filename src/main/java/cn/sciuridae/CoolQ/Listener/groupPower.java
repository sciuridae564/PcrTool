package cn.sciuridae.CoolQ.Listener;

public class groupPower {
    private boolean on=true;
    private boolean eggon=true;
    private boolean buton=true;


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

    @Override
    public String toString() {
        return "groupPower{" +
                "on=" + on +
                ", eggon=" + eggon +
                ", buton=" + buton +
                '}';
    }
}
