package cn.sciuridae.utils;

import java.sql.*;

public class InitDatabase {
    private static final int new_version =10;

    public void InitDB() {
        int version = -1;
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:root.db");
            PreparedStatement preparedStatement = conn.prepareStatement("select version from version");
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                version = set.getInt("version");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            //存在新建db文件的情况，sql查询可能出错
        }
        updataData(version, conn);
    }

    public void updataData(int version, Connection conn) {
        try {
            Statement statement = conn.createStatement();
            switch (version) {
                case -1://没有数据库版本标识，重新建立数据库
                    statement.executeUpdate("DROP TABLE IF EXISTS `pcrUnion`");
                    statement.executeUpdate("DROP TABLE IF EXISTS `teamMember`");
                    statement.executeUpdate("DROP TABLE IF EXISTS `knifeList`");
                    statement.executeUpdate("DROP TABLE IF EXISTS `tree`");
                    statement.executeUpdate("DROP TABLE IF EXISTS `progress`");
                    statement.executeUpdate("DROP TABLE IF EXISTS `Scores`");

                    statement.executeUpdate("CREATE TABLE  pcrUnion(id integer PRIMARY KEY AUTOINCREMENT,groupQQ integer(8),groupName varchar(256),groupMasterQQ integer(8),createDate datetime,teamSum integer(1))");
                    statement.executeUpdate("CREATE TABLE  teamMember(userQQ integer(8) PRIMARY KEY,groupQQ integer(8),name varchar(20),power boolean )");
                    statement.executeUpdate("CREATE TABLE  knifeList(id integer PRIMARY KEY AUTOINCREMENT, knifeQQ integer(8), hurt integer,date datetime,complete boolean, version integer ,loop integer(1),position integer(1) )");
                    statement.executeUpdate("CREATE TABLE  tree(teamQQ integer(8) PRIMARY KEY,date datetime, isTree boolean,groupQQ integer(8) );");
                    statement.executeUpdate("CREATE TABLE progress(id integer PRIMARY KEY,teamQQ integer(8),loop integer(2) ,  serial integer(1), Remnant integer(8), startTime datetime,endTime datetime,deleted integer(1) ,version integer)");
                    statement.executeUpdate("CREATE TABLE  Scores(QQ integer PRIMARY KEY ,   iSign boolean default false,  score integer(8) default 0)");
                    statement.executeUpdate("create table  'version'( 'version' integer)");
                    statement.executeUpdate("insert into version values (" + new_version + ")");
                case 1:
                    statement.executeUpdate("alter table teamMember add column token varchar(20);");
                case 3:
                    statement.executeUpdate("alter table tree add column name varchar(20);");
                case 4:
                    try {
                        statement.executeUpdate("alter table tree add column name varchar(20);");
                    } catch (SQLException e) {
                        //某一版里好像把这个漏掉了，有的版本又没露
                    }
                case 5:
                    statement.executeUpdate("alter table Scores add column live1 integer;");
                    statement.executeUpdate("alter table Scores add column live2 integer;");
                    statement.executeUpdate("alter table Scores add column live3 integer;");
                case 6:
                    statement.executeUpdate("alter table Scores add column liveON boolean default true ;");
                case 7:
                    statement.executeUpdate("CREATE TABLE  qqGroup(group_number integer PRIMARY KEY ,welcome text,welcome_tri boolean)");
                case 8:
                    statement.executeUpdate("alter table knifeList add column list integer;");
                case 9:
                    statement.executeUpdate("CREATE TABLE echoMsg(flag integer PRIMARY KEY AUTOINCREMENT ,group_number integer ,qq_number integer ,msg varchar(1024),time datetime,echoOk boolean)");
                case 10:
                    statement.executeUpdate("drop TABLE echoMsg");

                    statement.executeUpdate("update version set version =" + new_version);

            }
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
