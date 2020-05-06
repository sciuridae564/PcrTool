package cn.sciuridae.sqLite;

import cn.sciuridae.bean.Group;
import cn.sciuridae.bean.FightStatue;
import cn.sciuridae.bean.teamMember;
import com.forte.qqrobot.sender.MsgSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static cn.sciuridae.constant.dateFormat;
import static cn.sciuridae.constant.knifeFrash;

public class DB {
    public static DB Instance =new DB();
    private  SqliteHelper h;
    private DB() { }
    public static DB getInstance(){
        return Instance;
    }

    /**
     * 初始化数据库
     */
    public void init() {
        try {
            h= new SqliteHelper("root.db");
            String initTable="CREATE TABLE if not exists _group(\n" +
                    "                       id integer PRIMARY KEY AUTOINCREMENT,\n" +
                    "                       groupid varchar(20),\n" +//工会所在qq群
                    "                       groupName varchar(20),\n" +//工会名
                    "                       groupMasterQQ varchar(20),\n" +//会长qq
                    "                       createDate varchar(8)\n" + //工会创建时间
                    "                    );\n" +
                    "                    CREATE TABLE if not exists teamMember(\n" +
                    "                       userQQ varchar(20),\n" + //qq号
                    "                       id integer,\n" + //所属工会主键
                    "                      name varchar(20),\n" + //昵称
                    "                       power boolean,\n" +//1 管理员
                    "                       );\n" +
                    "                    CREATE TABLE if not exists knife(\n" +
                    "                       knifeQQ varchar(20),\n" +//出刀人qq
                    "                       no integer,\n" +  //周目+几王
                    "                       hurt integer,\n" + //伤害
                    "                       date varchar(8)\n" +//时间
                    "                       );\n" +
                    "                    CREATE TABLE if not exists tree(\n" +
                    "                       userId varchar(20),\n" +
                    "                       date varchar(8),\n" +
                    "                       isTree boolean,\n" +
                    "CREATE TABLE if not exists progress(\n" +
                    "                       groupid varchar(20), \n" +//工会qq
                    "                        id int, \n" +//工会主键
                    "                       loop int , \n" +//周目
                    "                       serial int, \n" +//几王
                    "                       Remnant int \n" +//剩余血量
                    "                    );";

            h.executeUpdate(initTable);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //删除上一天未出完的刀
    public Map<String, LinkedList<String>> clearTree(){
        String sql;
        LocalDateTime localDateTime = LocalDateTime.now();
        HashMap<String,LinkedList<String>> map=new HashMap<>();
        if(localDateTime.getHour()<knifeFrash){
            localDateTime=localDateTime.plusDays(-1);
        }
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        sql="select userId groupid from tree, _group,teamMember where teamMember.id=_group.id and tree.userQQ=teamMember.userId and date<\""+df.format(localDateTime)+"\"";
        try {
            RowMapper<String[]>  rowMapper=new RowMapper<String[]>() {
                @Override
                public String[] mapRow(ResultSet rs, int index) throws SQLException {
                    String[] strings=new String[2];
                    strings[0]= rs.getString("userId");
                    strings[1]= rs.getString("groupid");
                    return strings;
                }
            };
            ArrayList<String[]> strings=(ArrayList<String[]>)h.executeQuery(sql,rowMapper);
            if(strings!=null) {  //有人挂树
                sql = "delete from tree where date<\"" + df.format(localDateTime) + "\"";
                h.executeUpdate(sql);//删了树
                for(String[] strings1:strings){
                    try {
                        map.get(strings1[1]).add(strings1[0]);
                    }catch (NullPointerException e){
                        LinkedList<String> linkedList=new LinkedList<String>();
                        linkedList.add(strings1[0]);
                        map.put(strings1[1],linkedList);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    //建团
    public synchronized int  creatGroup(Group group){
        String sql="select name from teamMember where userQQ="+group.getGroupMasterQQ();
        try {
            String i=h.executeQuery(sql,String.class);
            return 0;//已经有一个社团了
        } catch (SQLException e) {
            //没有找到
            String sql1="insert into _group values(null,"+group.getGroupid()+","+group.getGroupName()+","+group.getGroupMasterQQ()+","+group.getCreateDate()+")";
            try {
                h.executeUpdate(sql1);
                return 1;//创建成功
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return -1;
    }
    //加团
    public synchronized int joinGroup(teamMember teamMember ,String groupNum){
        String sql="select name from teamMember where userQQ="+teamMember.getUserQQ();//是否已经进过社团
        String sql1="select id from _group where groupid="+groupNum;//找社团主键
        try {
            String i=h.executeQuery(sql,String.class);
            return 0;//已经有社团不允许再进
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            try {
                Integer i=h.executeQuery(sql1,Integer.class);
                String sql3="select count(*) from teamMember where id="+i;
                int j=h.executeQuery(sql3,Integer.class);
                if(j>29){
                    return 1;//人满了不能进
                }
                String sql2="insert into teamMember values(" +
                        teamMember.getUserQQ()+","
                        +i+","
                        +teamMember.getName()+","
                        +teamMember.isPower()+","
                        +")";
                h.executeUpdate(sql2);//插入表中占个坑
                return 2;
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
    //退团
    public synchronized int outGroup(String userQQ) {
        String sql1 = "select id from _group where userQQ=" + userQQ;//找社团主键
        try {
            int i = h.executeQuery(sql1, Integer.class);
            String sql3="delete from teamMember where userQQ="+userQQ;
            h.executeUpdate(sql3);//删除资料
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            return 0;//没社团退不了
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
        //其实是出刀
    public synchronized int joinTree(String QQ,int no,String groupQQ){
        String date=new SimpleDateFormat(dateFormat).format(new Date());
        String sql="select groupid from teamMember,_group where teamMember.id=_group.id and teamMember.userQQ="+QQ+";";
        String sql2="select count(*) from tree where userId="+QQ+";";
        String sql1="insert into tree values("+QQ+","+date+",false,"+no+");";
        try {
            String i=h.executeQuery(sql,String.class);
            if(!i.equals(groupQQ)){
                    return 1;//src.append("¿,他群间谍发现，建议rbq一周");
            }else {
                int j=h.executeQuery(sql2,Integer.class);
                if(j==1){
                    return 2;//src.append("¿,打咩，没有第二棵树能上了");
                }else {
                     h.executeUpdate(sql1);
                    return 3;//src.append("已挂东南枝");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //这才是确认上树了
    public synchronized int trueTree(String QQ,String groupQQ){
        String sql="select groupid from teamMember,_group where teamMember.id=_group.id and teamMember.userQQ="+QQ+";";
        String sql2="select count(*) from tree where userId="+QQ+";";
        String sql1="update tree set isTree=true where userId="+QQ;
        try {
            String i=h.executeQuery(sql,String.class);
            if(!i.equals(groupQQ)){//数据库要不就没 这人要不就是别的群里的
                return 1;//src.append("¿,他群间谍发现，建议rbq一周");
            }else {
                int j=h.executeQuery(sql2,Integer.class);
                if(j==1){//数据库有挂树的资料，是真的挂了
                    h.executeUpdate(sql1);
                    return 2;//src.append("已经挂牢了，不要想偷偷从树上溜走了哟♥");
                }else {

                    return 3;//src.append("啊这，俺寻思树上也妹你影啊");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return -1;
    }



    //找找今天还有哪个小朋友没有出刀
    public synchronized String searchVoidKnife(String QQ){
        String date=new SimpleDateFormat(dateFormat).format(new Date());
        String sql="select knifeQQ,count(*) from knife where date=\""+date+"\" group by knifeQQ;";
        String sql1="select userQQ  from teamMember where id=(select id from _group where groupid="+QQ+");";

        StringBuilder src=new StringBuilder("啊这，这波好像是没有搜到数据\n");

        ArrayList<voidKnife> list;
        ArrayList<voidKnife> list1;
        RowMapper<voidKnife> extractor1=new RowMapper<voidKnife>() {
            public voidKnife mapRow(ResultSet rs, int index) throws SQLException {
                voidKnife strings=new voidKnife();
                strings.setQQ(rs.getString("userQQ"));
                strings.setNum(3);
                return strings;
            }
        };
        RowMapper<voidKnife> extractor=new RowMapper<voidKnife>() {
            public voidKnife mapRow(ResultSet rs, int index) throws SQLException {
                voidKnife strings=new voidKnife();
                strings.setQQ(rs.getString("knifeQQ"));
                strings.setNum(rs.getInt("count(*)"));
                return strings;
            }
        };
        try {
            list=(ArrayList<voidKnife>) h.executeQuery(sql,extractor);
            list1=(ArrayList<voidKnife>) h.executeQuery(sql1,extractor1);
            int i=0;
            for (int j=0;j<list1.size();j++){
                if(list1.get(j).getQQ().equals(list.get(i).getQQ())){
                    list1.get(j).setNum( list1.get(j).getNum()-list.get(i).getNum());
                    if(list1.get(j).getNum()==0){
                        list1.remove(j);j--;
                    }
                    i++;
                }
            }
            src=new StringBuilder("统计如下:\n");
            for(voidKnife voidKnife:list1){
                src.append("[CQ:at,qq="+voidKnife.getQQ()+"] ,还没出"+voidKnife.getNum()+"刀\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(src);
        return src.toString();
    }
    class voidKnife{
        private String QQ;
        private int num;

        public String getQQ() {
            return QQ;
        }

        public void setQQ(String QQ) {
            this.QQ = QQ;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        @Override
        public String toString() {
            return "voidKnife{" +
                    "QQ='" + QQ + '\'' +
                    ", num=" + num +
                    '}';
        }
    }

    /**
     * 开始会战
     * @param applyQQ 提出开始会战的人
     * @param groupCode 消息所在群号
     */
    public synchronized int startFight(String groupCode,String applyQQ){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select count(*) from _group where groupid=\"").append(groupCode).append("\",groupMasterQQ=\"").append(applyQQ).append("\";");
        RowMapper<Integer> rowMapper= (rs, index) -> rs.getInt("count(*)");
        RowMapper<Integer> rowMapper1= (rs, index) -> rs.getInt("id");
        try {
            if(h.executeQuery(stringBuilder.toString(),rowMapper)!=null){
                stringBuilder.delete(0,stringBuilder.length());
                stringBuilder.append("select id from _group where groupid=\"").append(groupCode).append("\";");
                List<Integer> integerList=h.executeQuery(stringBuilder.toString(),rowMapper1);
                if(integerList!=null){
                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("insert into progress values(").append(groupCode).append(",").append(integerList.get(0)).append(",1,1,-1");//-1为还未录入血量的状态
                    h.executeUpdate(stringBuilder.toString());
                    return 1;//成功开始
                }
                return 2;//已经开始了
            }
            return 0;//数据库离还没建这个工会或者没这权限
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 结束会展命令
     * @param groupCode 同上
     * @param applyQQ
     * @return
     */
    public synchronized int endFight(String groupCode,String applyQQ){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select count(*) from _group where groupid=\"").append(groupCode).append("\",groupMasterQQ=\"").append(applyQQ).append("\";");
        RowMapper<Integer> rowMapper= (rs, index) -> rs.getInt("count(*)");
        RowMapper<Integer> rowMapper1= (rs, index) -> rs.getInt("id");
        try {
            if(h.executeQuery(stringBuilder.toString(),rowMapper)!=null){
                stringBuilder.delete(0,stringBuilder.length());
                stringBuilder.append("select id from _group where groupid=\"").append(groupCode).append("\";");
                List<Integer> integerList=h.executeQuery(stringBuilder.toString(),rowMapper1);//工会主键
                if(integerList==null){
                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("delete from progress where id=").append(integerList.get(0));//删除进度条记录
                    h.executeUpdate(stringBuilder.toString());
                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("delete from tree where userId in(select userQQ from teamMember where id=").append(integerList.get(0)).append(")");//删除树上的人
                    h.executeUpdate(stringBuilder.toString());
                    return 1;//删除成功
                }
                return 2;//没可以删的东西
            }
            return 0;//数据库离还没建这个工会或者没这权限
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 提交伤害
     * @param QQ 出刀人的qq
     * @return
     */
    public synchronized void hurtfight(String QQ,int hurt,MsgSender sender){
        StringBuilder stringBuilder=new StringBuilder("select loop,serial,Remnant,teamMember.id  from teamMember ,progress where userQQ=\"");
        stringBuilder.append(QQ).append("\" and progress.id=teamMember.id");
        StringBuilder tips=new StringBuilder();
        RowMapper<Integer[]> rowMapper= (rs, index) -> {
            Integer[] i=new Integer[3];
            i[0]=rs.getInt("loop");i[1]=rs.getInt("serial");i[2]=rs.getInt("Remnant");i[3]=rs.getInt("teamMember.id");
            return i;
        };//获取boss属性
        RowMapper<String> rowMapper1= (rs, index) -> rs.getString("userId");//获取被救下来人的qq号
        RowMapper<String> rowMapper2= (rs, index) -> rs.getString("groupid");//获取通知的群号
        try {
            List<Integer[]> list=h.executeQuery(stringBuilder.toString(),rowMapper);//获取现在boss的生命，周目
            if(list!=null){
                //下树
                stringBuilder.delete(0,stringBuilder.length());
                stringBuilder.append("delete from tree where userId=\""+QQ+"\"");
                h.executeUpdate(stringBuilder.toString());

                //找这个工会的群号
                stringBuilder.delete(0,stringBuilder.length());
                stringBuilder.append("select groupid from _group where id=").append(list.get(0)[3]);
                List<String> list2=h.executeQuery(stringBuilder.toString(),rowMapper2);
                String groupQQ=list2.get(0);

                //计算boss血量，分成打爆处理（有救树流程）和没打爆处理
                int hurt_active;
                if(list.get(0)[3]-hurt>0){
                    hurt_active=hurt;
                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("update progress set ").append("Remnant=").append(hurt_active).append(" where id=\"").append(list.get(0)[0]*10+list.get(0)[1]).append("\"");
                    h.executeUpdate(stringBuilder.toString());
                    //没打穿boss
                }else {
                    hurt_active=-1;//伤害打穿了，进入下一模式
                    list.get(0)[1]=list.get(0)[1]==5?1:list.get(0)[1]+1;
                    list.get(0)[0]=list.get(0)[1]==5?list.get(0)[0]+1:list.get(0)[0];
                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("update progress set loop= ").append(list.get(0)[1]).append(",serial=").append(list.get(0)[1]).append(",Remnant=").append(hurt_active).append(" where id=\"").append(list.get(0)[0]*10+list.get(0)[1])     .append("\"");
                    h.executeUpdate(stringBuilder.toString());
                    //进入救树模式
                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("select userId from tree where  userId in(select userQQ from teamMember where id="+list.get(0)[3]+")");
                    List<String> list1=h.executeQuery(stringBuilder.toString(),rowMapper1);

                    tips.append("下树啦，下树啦");
                    for(String s:list1){
                        tips.append("[CA:at,qq=").append(s).append("] ");
                    }

                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("delete from tree where  userId in(select userQQ from teamMember where id="+list.get(0)[3]+")");
                    h.executeUpdate(stringBuilder.toString());
                    //这刀打爆了boss
                }
                //交成绩
                stringBuilder.delete(0,stringBuilder.length());
                stringBuilder.append("insert into knife values(" + QQ + ",").append(list.get(0)[1] + list.get(0)[0] * 10).append(",").append(hurt_active).append(",\"").append( new SimpleDateFormat(dateFormat).format(new Date())).append("\")");
                h.executeUpdate(stringBuilder.toString());
                tips.append("已交刀,[CQ:at,qq=").append(QQ).append("]");
                sender.SENDER.sendGroupMsg(groupQQ,tips.toString());//将救下树的人通知
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 查看今天所有人的出刀情况
     * @param QQ 查看人的QQ
     * @param time 查看的时间
     * @return
     */
    public synchronized Map<String,Integer> searchKnife(String QQ,String time){
        String sql="select userQQ from teamMember where id=(select id from userQQ=\""+QQ+"\");";
        RowMapper<String> req= (rs, index) -> rs.getString("userQQ");
        RowMapper<String[]> req1= (rs, index) -> {
            String[] strings=new String[2];
            strings[0]=rs.getString("knifeQQ");
            strings[1]=rs.getString("hurt");
            return strings;
        };
        HashMap<String,Integer> hashMaps=null;
        try {
            List<String> list=h.executeQuery(sql,req);
            if(list!=null){
                hashMaps=new HashMap<>();//用户qq号，出刀次数
                for (String s:list){
                    hashMaps.put(s,0);
                }
                sql="select hurt,knifeQQ  from knife where knifeQQ in ("+"select userQQ from teamMember where id=(select id from userQQ=\""+QQ+"\") and date=\""+new SimpleDateFormat(dateFormat).format(new Date())+"\")";
                List<String[]> list1=h.executeQuery(sql,req1);
                for(String[] strings:list1) {
                    hashMaps.put(strings[0], hashMaps.get(hashMaps) + 1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return hashMaps;
    }

    /**
     * 查看现在的boss状态
     * @param QQ 查询人的qq
     * @return
     */
    public synchronized FightStatue searchFightStatue(String QQ ){
        FightStatue fightStatue=null;
        String sql="select loop,serial,Remnant from progress where id=(select id from userQQ=\""+QQ+"\");";
        RowMapper<FightStatue> rowMapper= new RowMapper<FightStatue>() {
            @Override
            public FightStatue mapRow(ResultSet rs, int index) throws SQLException {
                FightStatue fightStatue1=new FightStatue(rs.getInt("loop"),rs.getInt("serial"),rs.getInt("Remnant"));
                return fightStatue1;
            }
        };

        try {
            List<FightStatue> statues=h.executeQuery(sql,rowMapper);
            if(statues!=null){
                fightStatue=statues.get(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return fightStatue;
    }
}

