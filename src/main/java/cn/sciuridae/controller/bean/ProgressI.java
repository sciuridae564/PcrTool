package cn.sciuridae.controller.bean;

import cn.sciuridae.dataBase.bean.Progress;

public class ProgressI extends Progress {

    private int dem;

    public ProgressI(Progress progress, int dem) {
        super(progress.getId(), progress.getTeamQQ(), progress.getLoop(), progress.getSerial(), progress.getRemnant(), progress.getStartTime(), progress.getEndTime(), progress.getDeleted(), progress.getVersion());
        this.dem = dem;
    }

    public int getDem() {
        return dem;
    }

    public void setDem(int dem) {
        this.dem = dem;
    }
}
