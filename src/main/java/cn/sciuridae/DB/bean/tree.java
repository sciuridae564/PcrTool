package cn.sciuridae.DB.bean;

//说是树表其实是把正在出刀的人都集中在这里了
public class tree {
    private String userId;
    private String date;
    private boolean isTree;//真正挂树的人在这里
    private int no;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isTree() {
        return isTree;
    }

    public void setTree(boolean tree) {
        isTree = tree;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "tree{" +
                "userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", isTree=" + isTree +
                ", no=" + no +
                '}';
    }
}
