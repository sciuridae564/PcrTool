package cn.sciuridae.listener;

import cn.sciuridae.dataBase.bean.KnifeList;

public class KnifeState {
    private String msg;
    private boolean isOk;
    private String failMsg;
    private KnifeList knifeList;

    public KnifeList getKnifeList() {
        return knifeList;
    }

    public void setKnifeList(KnifeList knifeList) {
        this.knifeList = knifeList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getFailMsg() {
        return failMsg;
    }

    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
    }
}