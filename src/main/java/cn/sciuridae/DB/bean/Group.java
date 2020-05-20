package cn.sciuridae.DB.bean;

public class Group {
    private int id;//主键
    private String groupid;
    private String groupName;
    private String groupMasterQQ;
    private String createDate;

    public Group(int id, String groupid, String groupName, String groupMasterQQ, String createDate) {
        this.id = id;
        this.groupid = groupid;
        this.groupName = groupName;
        this.groupMasterQQ = groupMasterQQ;
        this.createDate = createDate;
    }

    public Group() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupMasterQQ() {
        return groupMasterQQ;
    }

    public void setGroupMasterQQ(String groupMasterQQ) {
        this.groupMasterQQ = groupMasterQQ;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupid='" + groupid + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupMasterQQ='" + groupMasterQQ + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}
