package cn.sciuridae.utils.bean;

public class PricnessConfig {
    private String tixingmaiyao;//提醒买药小助手文件名
    private int gashaponMax;//抽卡上限
    private int gashaponcool;//抽卡冷却时间，以秒为单位
    private boolean PcrToonon;//总开关
    private boolean eggon;//扭蛋开关
    private boolean buton;//买药小助手提示
    private boolean horse;//赛马开关

    public PricnessConfig() {
    }

    public PricnessConfig(String tixingmaiyao, int gashaponMax, int gashaponcool, boolean pcrToonon, boolean eggon, boolean buton, boolean horse) {
        this.tixingmaiyao = tixingmaiyao;
        this.gashaponMax = gashaponMax;
        this.gashaponcool = gashaponcool;
        PcrToonon = pcrToonon;
        this.eggon = eggon;
        this.buton = buton;
        this.horse = horse;
    }

    public boolean isPcrToonon() {
        return PcrToonon;
    }

    public void setPcrToonon(boolean pcrToonon) {
        PcrToonon = pcrToonon;
    }

    public boolean isEggon() {
        return eggon;
    }

    public void setEggon(boolean eggon) {
        this.eggon = eggon;
    }

    public boolean isButon() {
        return buton;
    }

    public void setButon(boolean buton) {
        this.buton = buton;
    }

    public boolean isHorse() {
        return horse;
    }

    public void setHorse(boolean horse) {
        this.horse = horse;
    }

    public String getTixingmaiyao() {
        return tixingmaiyao;
    }

    public void setTixingmaiyao(String tixingmaiyao) {
        this.tixingmaiyao = tixingmaiyao;
    }

    public int getGashaponMax() {
        return gashaponMax;
    }

    public void setGashaponMax(int gashaponMax) {
        this.gashaponMax = gashaponMax;
    }

    public int getGashaponcool() {
        return gashaponcool;
    }

    public void setGashaponcool(int gashaponcool) {
        this.gashaponcool = gashaponcool;
    }

    @Override
    public String toString() {
        return "PricnessConfig{" +
                "tixingmaiyao='" + tixingmaiyao + '\'' +
                ", gashaponMax=" + gashaponMax +
                ", gashaponcool=" + gashaponcool +
                '}';
    }
}
