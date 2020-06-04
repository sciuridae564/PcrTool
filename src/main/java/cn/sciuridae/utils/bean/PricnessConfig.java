package cn.sciuridae.utils.bean;

public class PricnessConfig {
    private String tixingmaiyao;//提醒买药小助手文件名
    private int gashaponMax;//抽卡上限
    private int gashaponcool;//抽卡冷却时间，以秒为单位

    public PricnessConfig() {
    }

    public PricnessConfig(String tixingmaiyao, int gashaponMax, int gashaponcool) {
        this.tixingmaiyao = tixingmaiyao;
        this.gashaponMax = gashaponMax;
        this.gashaponcool = gashaponcool;
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


}
