package cn.sciuridae.DB.sqLite;

import cn.sciuridae.DB.bean.*;
import com.forte.qqrobot.sender.MsgSender;
import org.apache.poi.ss.usermodel.Row;
import org.sqlite.SQLiteException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cn.sciuridae.Tools.stringTool.getBetweenDates;
import static cn.sciuridae.constant.*;

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
                    "                       power boolean\n" +//1 管理员
                    "                       );\n" +
                    "                    CREATE TABLE if not exists knife(\n" +
                    "                       knifeQQ varchar(20),\n" +//出刀人qq
                    "                       no integer,\n" +  //周目+几王
                    "                       hurt integer,\n" + //伤害
                    "                       date varchar(8),\n" +//时间
                    "                       complete boolean\n" +//是否是完整一刀（打爆boss的刀不算完整一刀
                    "                       );\n" +
                    "                    CREATE TABLE if not exists tree(\n" +
                    "                       userId varchar(20),\n" +
                    "                       date varchar(8),\n" +
                    "                       isTree boolean);\n" +
                    "CREATE TABLE if not exists progress(\n" +
                    "                       groupid varchar(20), \n" +//工会qq
                    "                        id int, \n" +//工会主键
                    "                       loop int , \n" +//周目
                    "                       serial int, \n" +//几王
                    "                       Remnant int \n" +//剩余血量
                    "                    );" +
                    " ALTER TABLE progress ADD COLUMN startTime varchar(8);" +//会战开始时间
                    " ALTER TABLE progress ADD COLUMN endTime varchar(8);"//会战结束时间
                    ;
            h.executeUpdate(initTable);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {

        }
    }


    /**
     * 清空传入群组的树
     * @param Group 群主键
     * @return <群qq，未出完刀的qq>
     */
    public Map<String, List<String>> clearTree(List<Integer> Group) {
        String clearTree = "delete from tree join teamMember on teamMember.userQQ=tree.userId where teamMember.id=";
        Map<String, List<String>> map = new HashMap<>();
        String clearMembers = "select userId from tree join teamMember on teamMember.userQQ=tree.userId where teamMember.id=";
        String selectGroupName = "select groupid from _group where id=";
        RowMapper<String> GroupQQs = (rs, index) -> rs.getString("groupid");
        RowMapper<String> Members = (rs, index) -> rs.getString("userId");
        for (int groupID : Group) {
            try {
                map.put(h.executeQuery(selectGroupName + groupID, GroupQQs).get(0), h.executeQuery(clearMembers + groupID, Members));
                h.executeUpdate(clearTree + groupID);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                //这，有主键为什么还找不着…………
            }
        }
        return map;
    }


    /**
     * 找结束会战的工会
     *
     * @return 工会qq号
     */
    public List<Integer> searchDeadLineGroup() {
        String time = new SimpleDateFormat(dateFormat).format(new Date());//现在时间
        String sql = "select id from progress where endTime<=\"" + time + "\"";
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("id");
        List<Integer> list = null;
        try {
            list = h.executeQuery(sql, rowMapper);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查找在会战期间的工会
     *
     * @return 工会qq号
     */
    public List<Integer> searchAllGroupOnProgress() {
        String time = new SimpleDateFormat(dateFormat).format(new Date());//现在时间
        String sql = "select id from progress  where startTime<=\"" + time + "\" and endTime>\"" + time + "\"";
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("id");
        List<Integer> list = null;
        try {
            list = h.executeQuery(sql, rowMapper);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 清除会战结束的boss进度
     *
     * @param groupid 会战结束的会战主键
     */
    public void deleteDeadLineGroup(List<Integer> groupid) {
        String sql = "delete from progress where id=";

        for (int i : groupid) {
            try {
                h.executeUpdate(sql + i);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 找过期的树表项,因为时间原因不包括工会战过期了的表项
     *
     * @return qq号
     */
    public List<String> searchOutTimeTree() {
        String time = new SimpleDateFormat(dateFormat).format(new Date());//现在时间
        Date date = new Date();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy:MM:dd");
        List<String> QQs = null;
        if (localDateTime.getHour() < knifeFrash) { //5点前不许不许刷新
            localDateTime = localDateTime.plusDays(-1);
        }
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        String sql = "select userId from tree where date=\"" + localDateTime.format(dateTimeFormatter) + "\"";
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("userId");

        try {
            QQs = h.executeQuery(sql, rowMapper);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return QQs;
    }


    /**
     * 删除数据库中过期的刀，在机器人启动时使用（防止长期不关数据没清除
     *
     * @return <群qq，未出完刀的qq>
     */
    public Map<String, List<String>> clearTree() {
        String sql;
        Date date =new  Date();
        LocalDateTime localDateTime =LocalDateTime.now();
        HashMap<String, List<String>> map = new HashMap<>();
        if(localDateTime.getHour()<knifeFrash){
            localDateTime=localDateTime.plusDays(-1);
        }
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);//设置日期格式
        sql="select userId groupid from tree, _group,teamMember where teamMember.id=_group.id and tree.userId=teamMember.userQQ and date<\""+df.format(date)+"\"";
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
                sql = "delete from tree where date<\"" + df.format(date) + "\"";
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
        } catch (SQLiteException exceptione){

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    //建团
    public synchronized int  creatGroup(Group group,String masterName){
        String sql="select name from teamMember where userQQ="+group.getGroupMasterQQ();
        RowMapper<Integer> rowMapper= (rs, index) -> rs.getInt("id");
        try {
            String i=h.executeQuery(sql,String.class);
            return 0;//已经有一个社团了
        } catch (SQLException e) {
            //没有找到
            String sql1="insert into _group values(null,\""+group.getGroupid()+"\",\""+group.getGroupName()+"\",\""+group.getGroupMasterQQ()+"\",\""+group.getCreateDate()+"\")";
            try {
                h.executeUpdate(sql1);
                sql1="select id from _group where groupid=\""+group.getGroupid()+"\"";
                int i=h.executeQuery(sql1,rowMapper).get(0);
                teamMember teamMember=new teamMember(group.getGroupMasterQQ(),true,i, masterName);
                //把团长加进去，默认创的人是团长
                if (joinGroup(teamMember, group.getGroupName()) == 0) {
                    return 0;//已经有一个社团了
                }
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
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return -1;
    }

    public synchronized int  creatGroup(Group group,String masterName,teamMember teamMember){
        String sql="select name from teamMember where userQQ="+group.getGroupMasterQQ();
        RowMapper<Integer> rowMapper= (rs, index) -> rs.getInt("id");
        try {
            String i=h.executeQuery(sql,String.class);
            return 0;//已经有一个社团了
        } catch (SQLException e) {
            //没有找到
            String sql1="insert into _group values(null,\""+group.getGroupid()+"\",\""+group.getGroupName()+"\",\""+group.getGroupMasterQQ()+"\",\""+group.getCreateDate()+"\")";
            try {
                System.out.println(sql1);
                h.executeUpdate(sql1);
                sql1="select id from _group where groupid=\""+group.getGroupid()+"\"";
                int i=h.executeQuery(sql1,rowMapper).get(0);
                teamMember teamMember1=new teamMember(group.getGroupMasterQQ(),true,i, masterName);
                joinGroup(teamMember1,group.getGroupName());//把团长加进去，默认创的人是团长
                teamMember.setId(i);
                joinGroup(teamMember,group.getGroupName());
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
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return -1;
    }

    //加团
    public synchronized int joinGroup(teamMember teamMember, String groupQQ) {
        String sql="select name from teamMember where userQQ=\""+teamMember.getUserQQ()+"\"";//是否已经进过社团
        String sql1 = "select id from _group where groupid=\"" + groupQQ + "\"";//找社团主键
        RowMapper<Integer> rowMapper= (rs, index) -> rs.getInt("id");
        RowMapper<Integer> rowMapper1= (rs, index) -> rs.getInt("count(*)");
        RowMapper<String> rowMapper2= (rs, index) -> rs.getString("name");
        try {
            List<String> i=h.executeQuery(sql,rowMapper2);
            i.get(0);
            return 0;//已经有社团不允许再进
        } catch (IndexOutOfBoundsException e) {
            try {
                List<Integer> i = h.executeQuery(sql1, rowMapper);
                String sql3 = "select count(*) from teamMember where id=" + i.get(0);
                List<Integer> j = h.executeQuery(sql3, rowMapper1);

                if (j.get(0) > 30) {
                    return 1;//人满了不能进
                }
                System.out.println(teamMember.getName() != null && teamMember.getName().length() != 0 ? pcrGroupMap.get(teamMember.getUserQQ()) : teamMember.getName());
                System.out.println(teamMember.getName() );
                String sql2 = "insert into teamMember(userQQ,id,name,power) values(\"" +
                        teamMember.getUserQQ() + "\","
                        + i.get(0) + ",\""
                        + (teamMember.getName() != null && teamMember.getName().length() != 0 ? pcrGroupMap.get(teamMember.getUserQQ()) : teamMember.getName()) + "\","
                        + (teamMember.isPower() ? 1 : 0)
                        + ")";
                h.executeUpdate(sql2);//插入表中占个坑
                return 2;
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (IndexOutOfBoundsException e2) {
                e.printStackTrace();
                return 3;//没有这个社团
            }
        }catch (ClassNotFoundException e) {
                e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    //退团
    public synchronized int outGroup(String userQQ) {
        String sql1 = "select id from teamMember where userQQ=\"" + userQQ + "\"";//找社团主键
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("id");
        try {
            List<Integer> i = h.executeQuery(sql1, rowMapper);
            i.get(0);
            String sql3="delete from teamMember where userQQ="+userQQ;
            h.executeUpdate(sql3);//删除资料
            String sql4 = "delete from tree where userId=" + userQQ;
            h.executeUpdate(sql4);//删除资料
            return 0;
        } catch (SQLException | NullPointerException | IndexOutOfBoundsException e) {
            return 1;//没社团退不了
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
        //其实是出刀
        public synchronized int joinTree(String QQ) {
        String date=new SimpleDateFormat(dateFormat).format(new Date());
        String sql="select groupid from teamMember,_group where teamMember.id=_group.id and teamMember.userQQ="+QQ+";";
            String sql2 = "select date from tree where userId=" + QQ + ";";
            String sql1 = "insert into tree values(\"" + QQ + "\",\"" + date + "\",0" + ");";
            RowMapper<String> rowMapper = (rs, index) -> rs.getString("groupid");
            RowMapper<String> rowMapper1 = (rs, index) -> rs.getString("date");
        try {
            h.executeQuery(sql, rowMapper).get(0);
            if (h.executeQuery(sql2, rowMapper1).size() != 0) {
                return 2;//src.append("¿,打咩，没有第二棵树能上了");
            }
            h.executeUpdate(sql1);
            return 3;//src.append("已挂东南枝");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            return 1;//这个是数据库没有数据
        }
        return -1;
    }

    //这才是确认上树了
    public synchronized int trueTree(String QQ,String groupQQ){
        String sql = "select groupid from teamMember,_group where teamMember.id=_group.id and teamMember.userQQ=\"" + QQ + "\";";
        String sql2 = "select date from tree where userId=\"" + QQ + "\";";
        String sql1 = "update tree set isTree=1 where userId=\"" + QQ + "\"";
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("groupid");
        RowMapper<String> rowMapper1 = (rs, index) -> rs.getString("date");
        try {
            String i = h.executeQuery(sql, rowMapper).get(0);//获取工会群号
            List<String> list = h.executeQuery(sql2, rowMapper1);
            if (list.size() != 0) {//数据库有挂树的资料，是真的挂了
                h.executeUpdate(sql1);
                return 2;//src.append("已经挂牢了，不要想偷偷从树上溜走了哟♥");
            } else {

                return 3;//src.append("啊这，俺寻思树上也妹你影啊");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {//获取工会群号失败
            return 1;
        }
        return -1;
    }

    /**
     * 转移工会长权限
     */
    public synchronized int changeGroupMaster(String oldQQ, String newQQ) {
        String oldID = "select id from teamMember where userQQ=\"" + oldQQ + "\" ";
        String newID = "select id from teamMember where userQQ=\"" + newQQ + "\" ";
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("id");
        RowMapper<Integer> rowMapper1 = (rs, index) -> rs.getInt("id");
        try {
            int id1 = h.executeQuery(newID, rowMapper).get(0);
            int id2 = h.executeQuery(oldID, rowMapper).get(0);
            if (id1 == id2) {
                //只有两个人在同一个工会才可以
                String change1 = "update _group set groupMasterQQ=\"" + newQQ + "\" where id=" + id1;//转让会长
                String change2 = "update _group set power=1 where userQQ=\"" + newQQ + "\"";//设置 b为管理员
                h.executeUpdate(change1, change2);//改变管理员权限
                return 2;//成功
            }
            return 1;//俩人不在同一个工会
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            //数据库没有新人的数据
        }
        return -1;
    }

    /**
     * 撤管理权限
     */
    public synchronized int downAdmin(String superPowerQQ, String newQQ) {
        String oldID = "select id from teamMember where userQQ=\"" + superPowerQQ + "\" ";
        String newID = "select id from teamMember where userQQ=\"" + newQQ + "\" ";
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("id");
        RowMapper<Integer> rowMapper1 = (rs, index) -> rs.getInt("id");
        try {
            int id1 = h.executeQuery(newID, rowMapper).get(0);
            int id2 = h.executeQuery(oldID, rowMapper).get(0);
            if (id1 == id2) {
                //只有两个人在同一个工会才可以
                String change2 = "update _group set power=0 where userQQ=\"" + newQQ + "\"";//设置 b为de管理员
                h.executeUpdate(change2);//改变管理员权限
                return 2;//成功
            }
            return 1;//俩人不在同一个工会
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            //数据库没有新人的数据
        }
        return -1;
    }

    public boolean isSuperPower(String groupQQ, String QQ) {
        String sql = "select groupid from _group where groupid=\"" + groupQQ + "\" and groupMasterQQ" + QQ + "\"";
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("groupid");

        try {
            if (h.executeQuery(sql, rowMapper).size() > 0) {//找着了
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 找找今天还有哪个小朋友没有出刀
     *
     * @param GroupQQ
     * @param mode    1为工会qq2为私人qq
     * @return
     */
    public synchronized HashMap<String, Integer> searchVoidKnifeByGroup(String GroupQQ, int mode) {
        String date=new SimpleDateFormat(dateFormat).format(new Date());
        HashMap<String, Integer> map = null;
        String sql1 = "select userQQ from teamMember where id =(select id from _group where groupid =\"" + GroupQQ + "\");";
        String sql2 = "select id from _group where groupid=\"" + GroupQQ + "\"";

        StringBuilder src=new StringBuilder("啊这，这波好像是没有搜到数据\n");
        RowMapper<String> extractor1 = (rs, index) -> rs.getString("userQQ");//这个工会的成员qq
        RowMapper<String> mapper = (rs, index) -> rs.getString("startTime");//随便获取个值，证明这个工会正在打boss
        RowMapper<Integer> mapper1 = (rs, index) -> rs.getInt("id");//这个工会的主键
        try {
            if (1 == mode) {
                //整个工会查找
                List<Integer> list2 = h.executeQuery(sql2, mapper1);//这个工会的主键
                String sql3 = "select startTime  from progress where id=\"" + list2.get(0) + "\"";//获取这个工会战开始时间
                List<String> list3 = h.executeQuery(sql3, mapper);
                if (list3.get(0).compareTo(date) < 0) {//没boss进度，或还没到出刀时间
                    return null;
                }
                List<Knife> knifes = searchKnife(null, GroupQQ, date);//今天的出刀表
                List<String> list1 = h.executeQuery(sql1, extractor1);//工会成员全员空刀表
                map = new HashMap<>();
                for (String s : list1) {
                    map.put(s, 3);
                }
                for (Knife knife : knifes) {
                    if (knife.isComplete()) {
                        map.put(knife.getKnifeQQ(), map.get(knife.getKnifeQQ()) - 1);
                    }
                }
            } else {
                map = (HashMap<String, Integer>) searchKnife(GroupQQ, date);//今天的出刀表
                Set<String> set = map.keySet();
                for (String s : set) {
                    map.put(s, 3 - map.get(s));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {//没找着出刀信息
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 找找今天还有哪个小朋友没有出刀,工会id版
     *
     * @param Groupid//工会主键
     * @return
     */
    public synchronized Map<String, Integer> searchVoidKnifeByGroup(int Groupid, String date) {
        HashMap<String, Integer> map = null;
        String sql1 = "select userQQ from teamMember where id =" + Groupid;
        String sql2 = "select groupid from _group where id=" + Groupid;

        RowMapper<String> extractor1 = (rs, index) -> rs.getString("userQQ");//这个工会的成员qq
        RowMapper<String> mapper = (rs, index) -> rs.getString("startTime");//随便获取个值，证明这个工会正在打boss
        RowMapper<String> mapper1 = (rs, index) -> rs.getString("groupid");//
        try {
            String sql3 = "select startTime  from progress where id=\"" + Groupid + "\"";//获取这个工会战开始时间
            List<String> list3 = h.executeQuery(sql3, mapper);
            if (list3.get(0).compareTo(date) > 0) {//没boss进度，或还没到出刀时间
                return null;
            }

            List<Knife> knifes = searchKnife(null, h.executeQuery(sql2, mapper1).get(0), date);//今天的出刀表

            List<String> list1 = h.executeQuery(sql1, extractor1);//工会成员全员空刀表
            map = new HashMap<>();
            for (String s : list1) {
                map.put(s, 3);
            }
            for (Knife knife : knifes) {
                if (knife.isComplete()) {
                    map.put(knife.getKnifeQQ(), map.get(knife.getKnifeQQ()) - 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {//没找着出刀信息
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 开始会战
     * @param applyQQ 提出开始会战的人
     * @param groupCode 消息所在群号
     */
    public synchronized int startFight(String groupCode, String applyQQ, String date) {
        StringBuilder stringBuilder=new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        if (date.equals("")) {
            date = simpleDateFormat.format(new Date());
        }
        //获取8天后日期
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(date);//日期输入格式错误
        } catch (ParseException e) {
            return 3;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.DATE, 8);
        String endDate = simpleDateFormat.format(calendar.getTime());
        RowMapper<Integer> rowMapper1= (rs, index) -> rs.getInt("id");
        if (powerCheck(applyQQ, groupCode)) {
            try {
                stringBuilder.append("select id from _group where groupid=\"").append(groupCode).append("\";");
                List<Integer> integerList = h.executeQuery(stringBuilder.toString(), rowMapper1);
                if (integerList != null) {
                    stringBuilder.delete(0, stringBuilder.length());
                    stringBuilder.append("insert into progress values(").append(groupCode).append(",").append(integerList.get(0)).append(",1,1,").append(getBossHpLimit(0)).append(",\"").append(date).append("\",\"").append(endDate).append("\")");//-1为还未录入血量的状态

                    h.executeUpdate(stringBuilder.toString());
                    if (date.equals(simpleDateFormat.format(new Date()))) {
                        return 1;//成功开始,且就在这一天
                    }
                    return 4;
                }
                return 2;//已经开始了

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return 0;//数据库离还没建这个工会或者没这权限
            }
        }
        return -1;
    }

    /**
     * 查询工会名字
     *
     * @param QQ
     * @param mode 1为根据这个人的qq，2为根据这个群的qq
     * @return
     */
    public synchronized String searchGroupNameByQQcode(String QQ, int mode) {
        String groupName = null;
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("groupName");
        try {
            switch (mode) {
                case 1:
                    String sql1 = "select groupName  from _group,teamMember where _group.id= teamMember.id and userQQ=\"" + QQ + "\"";
                    groupName = h.executeQuery(sql1, rowMapper).get(0);
                    break;
                case 2:
                    String sql2 = "select groupName from _group where groupid=\"" + QQ + "\"";
                    groupName = h.executeQuery(sql2, rowMapper).get(0);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            //没查到返回空
        }
        return groupName;
    }

    /**
     * 查询工会名字
     *
     * @param id 工会主键
     * @return
     */
    public synchronized String searchGroupNameByID(int id) {
        String sql = "select groupid from _group where id=" + id;
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("groupid");

        try {
            return h.executeQuery(sql, rowMapper).get(0);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {

        }
        return null;
    }


    /**
     * 查询所给qq的游戏
     *
     * @param QQ
     * @return 获取昵称
     */
    public String searchName(String QQ) {
        String sql = "select name from teamMember where userQQ=\"" + QQ + "\"";
        RowMapper<String> rowMapper = new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getString("name");
            }
        };
        String name = null;
        try {
            name = h.executeQuery(sql, rowMapper).get(0);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {

        }
        return name;
    }

    /**
     * 结束会展命令
     * @param groupCode 同上
     * @param applyQQ
     * @return
     */
    public synchronized int endFight(String groupCode,String applyQQ){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("select count(*) from _group where groupid=\"").append(groupCode).append("\" and groupMasterQQ=\"").append(applyQQ).append("\";");
        RowMapper<Integer> rowMapper= (rs, index) -> rs.getInt("count(*)");
        RowMapper<Integer> rowMapper1= (rs, index) -> rs.getInt("id");
        try {
            if (h.executeQuery(stringBuilder.toString(), rowMapper).get(0)!=0){
                stringBuilder.delete(0,stringBuilder.length());
                stringBuilder.append("select id from _group where groupid=\"").append(groupCode).append("\";");
                System.out.println(stringBuilder);
                List<Integer> integerList=h.executeQuery(stringBuilder.toString(),rowMapper1);//工会主键
                if (integerList.size()!=0){
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
        boolean complete;
        RowMapper<Integer[]> rowMapper= (rs, index) -> {
            Integer[] i = new Integer[4];
            i[0] = rs.getInt("loop");
            i[1] = rs.getInt("serial");
            i[2] = rs.getInt("Remnant");
            i[3] = rs.getInt("id");
            return i;
        };//获取boss属性
        RowMapper<String> rowMapper1= (rs, index) -> rs.getString("userId");//获取被救下来人的qq号
        RowMapper<String> rowMapper2= (rs, index) -> rs.getString("groupid");//获取通知的群号
        RowMapper<Boolean> rowMapper3 = (rs, index) -> rs.getBoolean("complete");//获取最近一次的出刀是否为过盈尾刀，判断这次是否为补时刀

        try {
            List<Integer[]> list=h.executeQuery(stringBuilder.toString(),rowMapper);//获取现在boss的生命，周目

            //找这个工会的群号
            String string = "select groupid from _group where id=" + list.get(0)[3];
            List<String> list2 = h.executeQuery(string, rowMapper2);
            String groupQQ = list2.get(0);

            //查询这个人出没出完三刀
            if (!ningpeichudaoma(QQ)) {
                sender.SENDER.sendGroupMsg(groupQQ, "已经出完三刀惹");
                return;
            }

            if(list!=null){
                //下树
                stringBuilder.delete(0,stringBuilder.length());
                stringBuilder.append("delete from tree where userId=\""+QQ+"\"");
                h.executeUpdate(stringBuilder.toString());

                //计算boss血量，分成打爆处理（有救树流程）和没打爆处理
                int hurt_active;
                if (list.get(0)[2] - hurt>0){
                    hurt_active=hurt;
                    stringBuilder.delete(0, stringBuilder.length());
                    stringBuilder.append("update progress set ").append("Remnant=").append(list.get(0)[2] - hurt).append(" where id=").append(list.get(0)[3]);
                    h.executeUpdate(stringBuilder.toString());
                    complete = true;
                    //没打穿boss
                }else {
                    hurt_active = list.get(0)[2];//伤害打穿了，进入下一模式
                    int loop = (list.get(0)[1] == 5 ? list.get(0)[0] + 1 : list.get(0)[0]);
                    int serial = (list.get(0)[1] == 5 ? 1 : list.get(0)[1] + 1);
                    //更新boss
                    stringBuilder.delete(0, stringBuilder.length());
                    stringBuilder.append("update progress set loop= ").append(loop).append(",serial=").append(serial).append(",Remnant=").append(getBossHpLimit(serial - 1)).append(" where id=\"").append(list.get(0)[3]).append("\"");
                    h.executeUpdate(stringBuilder.toString());
                    //进入救树模式，把树上的人都噜下来
                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("select userId from tree where  userId in(select userQQ from teamMember where id="+list.get(0)[3]+")");
                    List<String> list1=h.executeQuery(stringBuilder.toString(), rowMapper1);

                    if (list1.size() > 0) {
                        tips.append("下树啦，下树啦");
                        for (String s : list1) {
                            tips.append("[CQ:at,qq=").append(s).append("] ");
                        }
                        sender.SENDER.sendGroupMsg(groupQQ, tips.toString());//将救下树的人通知
                    }

                    stringBuilder.delete(0,stringBuilder.length());
                    stringBuilder.append("delete from tree where  userId in(select userQQ from teamMember where id="+list.get(0)[3]+")");
                    h.executeUpdate(stringBuilder.toString());
                    //看看这刀是不是补时刀

                    stringBuilder.delete(0, stringBuilder.length());
                    stringBuilder.append("select no,complete from knife where knifeQQ=\"").append(QQ).append("\" order by no");
                    List<Boolean> list3 = h.executeQuery(stringBuilder.toString(), rowMapper3);

                    if (list3.size() != 0 && !list3.get(list3.size() - 1)) {
                        complete = true;//补时刀强制不可套娃
                    } else {
                        complete = false;
                    }
                    //这刀打爆了boss
                }
                //交成绩
                stringBuilder.delete(0, stringBuilder.length());
                stringBuilder.append("insert into knife values(" + QQ + ",").append(list.get(0)[1] + list.get(0)[0] * 10).append(",").append(hurt_active).append(",\"").append(GetPrincessTime(groupQQ)).append("\"," + (complete ? 1 : 0) + ")");
                h.executeUpdate(stringBuilder.toString());
                tips.delete(0, tips.length());
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
                sql = "select hurt,knifeQQ  from knife where complete=1 and knifeQQ in (" + "select userQQ from teamMember where id=(select id from userQQ=\"" + QQ + "\") and date=\"" + new SimpleDateFormat(dateFormat).format(new Date()) + "\")";
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
     * 查看今天所有人的出刀情况
     *
     * @param id   工会主键
     * @param time 查看的时间
     * @return
     */
    public synchronized Map<String, List<Knife>> searchKnife(int id, String time) {
        String sql = "select userQQ from teamMember where id=" + id;
        RowMapper<String> req = (rs, index) -> rs.getString("userQQ");
        RowMapper<Knife> req1 = (rs, index) -> {
            Knife knife = new Knife(rs.getString("knifeQQ"), rs.getInt("no"), rs.getInt("hurt"), rs.getBoolean("complete"));
            return knife;
        };
        HashMap<String, List<Knife>> hashMaps = null;
        try {
            List<String> list = h.executeQuery(sql, req);
            if (list != null) {
                hashMaps = new HashMap<>();//用户qq号，出刀次数
                for (String s : list) {
                    hashMaps.put(s, new ArrayList<>());
                }
                sql = "select knife.*  from knife where  knifeQQ in (select userQQ from teamMember where id=" + id + ") and date=\"" + time + "\" order by knife.no";

                List<Knife> list1 = h.executeQuery(sql, req1);
                for (Knife strings : list1) {
                    hashMaps.get(strings.getKnifeQQ()).add(strings);
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
        FightStatue fightStatue = null;
        String sql = "select loop,serial,Remnant,startTime from progress where id=(select id from teamMember where  userQQ=\"" + QQ + "\");";
        RowMapper<FightStatue> rowMapper= new RowMapper<FightStatue>() {
            @Override
            public FightStatue mapRow(ResultSet rs, int index) throws SQLException {
                FightStatue fightStatue1 = new FightStatue(rs.getInt("loop"),
                        rs.getInt("serial"),
                        rs.getInt("Remnant"),
                        rs.getString("startTime"));
                return fightStatue1;
            }
        };

        try {
            List<FightStatue> statues=h.executeQuery(sql,rowMapper);
            if (statues.size()!=0){
                fightStatue=statues.get(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return fightStatue;
    }

    public int getBossHpLimit(int ser) {
        return BossHpLimit[ser];
    }

    /**
     * 权限验证
     *
     * @return
     */
    public boolean powerCheck(String QQ, String groupQQ) {
        String sql = "select power from teamMember ,_group where userQQ=\"" + QQ + "\" and groupid=\"" + groupQQ + "\" and teamMember.id=_group.id";
        RowMapper<Boolean> rowMapper = (rs, index) -> rs.getBoolean("power");
        try {
            return h.executeQuery(sql, rowMapper).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {

        }
        return false;
    }

    /**
     * 解散工会
     *清空所有有关数据
     * @param groupQQ
     */
    public void dropGroup(String groupQQ) {
        String sql = "delete from teamMember where id =(select id from _group where groupid=\"" + groupQQ + "\")";
        String sql1 = "delete from _group where groupid = \"" + groupQQ + "\"";
        String sql2 = "delete from knife where knifeQQ in" +
                "( select userQQ from teamMember join _group on _group.id=teamMember.id where _group.groupid=\"" + groupQQ+"\")";
        try {
            h.executeUpdate(sql, sql1,sql2);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 改自己名字
     *
     * @param QQ
     * @return
     */
    public String changeName(String QQ, String newName) {
        String tips = "好像还没有加入过工会哎";
        String sql = "select name from teamMember where userQQ=\"" + QQ + "\"";
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("name");

        try {
            String name = h.executeQuery(sql, rowMapper).get(0);
            String sql_changeName = "update teamMember set name=\"" + newName + "\" where userQQ=\"" + QQ + "\"";
            h.executeUpdate(sql_changeName);

            tips = "改名成功，原名：" + name + "\n现在为：" + newName;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {

        }
        return tips;
    }

    /**
     * 改工会名字
     *
     * @param
     * @return
     */
    public String changeGroupName(String GroupQQ, String newGroupName) {
        String tips = "还没有建立工会呢";
        String sql = "select groupName from _group where groupid=\"" + GroupQQ + "\"";
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("groupName");

        try {
            String name = h.executeQuery(sql, rowMapper).get(0);
            String sql_changeName = "update _group set groupName=\"" + newGroupName + "\" where groupid=\"" + GroupQQ + "\"";
            h.executeUpdate(sql_changeName);

            tips = "改名成功，原名：" + name + "\n现在为：" + newGroupName;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {

        }
        return tips;
    }

    public String groupStatue(String groupQQ) {
        StringBuilder tips = new StringBuilder("还没有建立工会呢");
        String sql = "select * from _group where groupid=\"" + groupQQ + "\"";
        RowMapper<Group> groupId = (rs, index) -> new Group(rs.getInt("id"), rs.getString("groupid"), rs.getString("groupName"), rs.getString("groupMasterQQ"), rs.getString("createDate"));
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("count(*)");
        String sql1 = "select count(*) form teamMember join _group on _group.id=teamMember.id where _group.groupid=\"";
        try {
            Group group = h.executeQuery(sql, groupId).get(0);
            int num = h.executeQuery(sql1, rowMapper).get(0);
            tips.append("工会名：").append(group.getGroupName()).append("\n工会会长qq：").append(group.getGroupMasterQQ()).append("\n工会创建时间：").append(group.getCreateDate()).append("工会成员数：").append(num);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
        }
        return tips.toString();

    }

    public List<teamMember> groupMemberList(String groupQQ) {
        List<teamMember> members = null;
        String sql = "select id from _group where groupid=\"" + groupQQ + "\"";
        RowMapper<Integer> groupId = (rs, index) -> rs.getInt("id");
        RowMapper<teamMember> memebers = (rs, index) -> {
            teamMember teamMember = new teamMember(rs.getString("userQQ"), rs.getBoolean("power"), null, rs.getString("name"));
            return teamMember;
        };
        try {
            int id = h.executeQuery(sql, groupId).get(0);
            String searchGroupList = "select userQQ,name,power from teamMember where id=" + id;
            members = h.executeQuery(searchGroupList, memebers);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return members;
    }

    /**
     * 查询出刀情况
     *
     * @param QQ      和群qq择一使用
     * @param GroupQQ
     * @param time
     * @return
     */
    public List<Knife> searchKnife(String QQ, String GroupQQ, String time) {
        StringBuilder sql = new StringBuilder();
        if (QQ != null) {
            sql.append("select knife.rowid , knife.* from knife where knifeQQ=\"");
            sql.append(QQ).append("\"");
        } else {
            sql.append("select knife.rowid , knife.* from knife join teamMember on knife.knifeQQ=teamMember.userQQ join _group on _group.id=teamMember.id \n" + "where _group.groupid=\"").append(GroupQQ).append("\"");
        }
        if (time != null) {
            sql.append(" and knife.date=\"").append(time).append("\"");
        }
        RowMapper<Knife> knifes = (rs, index) -> {
            Knife knife = new Knife(rs.getString("knifeQQ"), rs.getInt("no"), rs.getInt("hurt"), rs.getString("date"), rs.getBoolean("complete"), rs.getInt("rowid"));
            return knife;
        };
        List<Knife> list = null;
        try {
            list = h.executeQuery(sql.toString(), knifes);
//            System.out.println(sql);
//            for(Knife s:list){
//                System.out.println(s);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;

    }

    public FightStatue getFightStatue(String groupQQ) {
        String selectBoss = "select loop,serial,Remnant from progress where groupid=\"" + groupQQ + "\"";
        RowMapper<FightStatue> rowMapper = (rs, index) -> {
            FightStatue fightStatue = new FightStatue(rs.getInt("loop"), rs.getInt("serial"), rs.getInt("Remnant"));
            return fightStatue;
        };
        try {
            FightStatue fightStatue = h.executeQuery(selectBoss, rowMapper).get(0);
            return fightStatue;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){

        }
        return null;
    }

    /**
     * @param loop
     * @param serial
     * @param Remnant
     * @return true为没有变周目，没有撸在上树的人
     */
    public boolean changeBoss(String groupQQ, int loop, int serial, int Remnant) {
        String selectBoss = "select loop,serial from progress where groupid=\"" + groupQQ + "\"";
        String changeBoss = "update progress set loop=" + loop + ",serial=" + serial + ",Remnant=" + Remnant + " where groupid=\"" + groupQQ + "\"";
        boolean is = false;
        RowMapper<FightStatue> rowMapper = (rs, index) -> {
            FightStatue fightStatue = new FightStatue(rs.getInt("loop"), rs.getInt("serial"), 1);
            return fightStatue;
        };

        try {
            FightStatue fightStatue = h.executeQuery(selectBoss, rowMapper).get(0);
            if (fightStatue.getLoop() == loop && fightStatue.getSerial() == serial) {//变周目时会把所有人撸下来
                is = true;
            } else {
                String getGroupId = "delete from tree where userId in ( select userId from teamMember join _group on teamMember.id=_group.id where _group.groupid=\"" + groupQQ + "\")";
                h.executeUpdate(getGroupId);
                is = false;
            }
            h.executeUpdate(changeBoss);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {

        }
        return is;
    }

    public boolean deleteKnife(int id) {
        String sql = "delete from knife where rowid=" + id;
        int i = 0;
        try {
            i = h.executeUpdate(sql);
            System.out.println(i);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return i == 1;
    }

    //检查这个qq号是否出过三刀了
    public boolean ningpeichudaoma(String QQ) {
        String sql = "select count(*) from knife where knifeQQ =\"" + QQ + "\" and " + "date=\"" + new SimpleDateFormat(dateFormat).format(new Date()) + "\" and complete=1";
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("count(*)");
        try {
            int knifesNum = h.executeQuery(sql, rowMapper).get(0);
            if (knifesNum > 2) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
        }
        return true;
    }

    /**
     * 设置工会管理员
     *
     * @param Tragit  将要获得权限的qq
     * @param groupQQ 群qq
     * @return
     */
    public int setAdmin(String Tragit, String groupQQ) {
        String sql = "update teamMember set power=1 where userQQ=\"" + Tragit + "\"";

        try {
            int i = h.executeUpdate(sql);
            return i;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param kickQQ
     * @return
     */
    public int deleteMember(String kickQQ) {
        String sql = "delete from teamMember where userQQ=\"" + kickQQ + "\"";
        try {
            return h.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 查在树上的人，是已经挂树的人
     *
     * @param GroupQQ
     * @return 获取挂树人的qq列表
     */
    public List<String> searchTree(String GroupQQ) {
        String sql = "select tree.* from tree join teamMember on teamMember.userQQ=tree.userId" +
                "                       join _group on _group.id=teamMember.id" +
                " where isTree=1 and _group.groupid=\"" + GroupQQ + "\"";
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("userId");
        List<String> trees = null;

        try {
            trees = h.executeQuery(sql, rowMapper);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return trees;
    }

    /**
     * 查询谁在出刀
     *
     * @param GroupQQ
     * @return 获取挂树人的qq列表
     */
    public List<String> searchOutKnife(String GroupQQ) {
        String sql = "select tree.* from tree join teamMember on teamMember.userQQ=tree.userId" +
                "                       join _group on _group.id=teamMember.id" +
                " where isTree=0 and _group.groupid=\"" + GroupQQ + "\"";
        RowMapper<String> rowMapper = (rs, index) -> rs.getString("userId");
        List<String> trees = null;

        try {
            trees = h.executeQuery(sql, rowMapper);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return trees;
    }


    /**
     * 获取这个工会公主连接工会战日期，即每天五点之后进入下一天，第一天是第一天，最后一天以12点截止
     *
     * @return
     */
    public String GetPrincessTime(String groupid) {
        String time = new SimpleDateFormat(dateFormat).format(new Date());
        String sql = "select startTime ,endTime from progress where groupid=\"" + groupid + "\"";
        RowMapper<String[]> rowMapper = new RowMapper<String[]>() {
            @Override
            public String[] mapRow(ResultSet rs, int index) throws SQLException {
                String[] strings = new String[2];
                strings[0] = rs.getString("startTime");
                strings[1] = rs.getString("endTime");
                return strings;
            }
        };

        try {
            String[] strings = h.executeQuery(sql, rowMapper).get(0);
            if (strings[0].compareTo(time) != 0) {
                if (strings[1].compareTo(time) != 0) {
                    //在工会战持续时间内
                    LocalDateTime localDateTime = LocalDateTime.now();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy:MM:dd");
                    List<String> QQs = null;
                    if (localDateTime.getHour() < knifeFrash) { //5点前不许不许刷新
                        localDateTime = localDateTime.plusDays(-1);
                    }
                    time = localDateTime.format(dateTimeFormatter);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        return time;
    }

    /**
     * 获得所有在会战进行中的工会id
     *
     * @return
     */
    public List<Integer> getAllGroupQQInFight() {
        String sql = "select _group.id from _group  join progress on progress.groupid=_group.groupid ";
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("id");

        try {
            List<Integer> groupQQs = h.executeQuery(sql, rowMapper);
            List<Integer> list = h.executeQuery(sql, rowMapper);

            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据主键查询这个工会工会战的开始到结束的时间列表
     *
     * @param id
     * @return
     */
    public List<Date> getDateList(int id) {
        String sql = "select startTime,endTime from progress where id=" + id;
        RowMapper<String[]> rowMapper = (rs, index) -> {
            String[] times = new String[2];
            times[0] = rs.getString("startTime");
            times[1] = rs.getString("endTime");
            return times;
        };

        try {
            String[] strings = h.executeQuery(sql, rowMapper).get(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy:MM:dd");
            List<Date> list = null;
            try {
                list = getBetweenDates(new Date(simpleDateFormat.parse(strings[0]).getTime() - 24 * 3600 * 1000), simpleDateFormat.parse(strings[1]));
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException | NullPointerException e) {

        }
        return null;
    }

    public int searchGroupIdByGroupQQ(String GroupQQ) {
        String sql = "select id from _group where groupid=\"" + GroupQQ + "\"";
        RowMapper<Integer> rowMapper = (rs, index) -> rs.getInt("id");

        try {
            return h.executeQuery(sql, rowMapper).get(0);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {

        }
        return -1;
    }

}

