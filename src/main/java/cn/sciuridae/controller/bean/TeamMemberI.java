package cn.sciuridae.controller.bean;

import cn.sciuridae.dataBase.bean.TeamMember;

public class TeamMemberI extends TeamMember {
    private int intPower;

    //1 2 3
    public TeamMemberI(TeamMember teamMember, boolean isSuper) {
        super(teamMember.getUserQQ(), teamMember.getGroupQQ(), teamMember.getName(), teamMember.getPower());

        if (isSuper) {
            this.intPower = 3;
        } else if (teamMember.getPower()) {
            this.intPower = 2;
        } else {
            intPower = 1;
        }
    }

    public int getIntPower() {
        return intPower;
    }

    public void setIntPower(int intPower) {
        this.intPower = intPower;
    }
}
