package cn.sciuridae.utils;

import java.sql.*;

public class InitDatabase {
    private static final int new_version = 1;
    private static final String LINE_SEPARATOR = System.lineSeparator();

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

                    statement.executeUpdate("CREATE TABLE if not exists pcrUnion(id integer PRIMARY KEY AUTOINCREMENT,groupQQ integer(8),groupName varchar(256),groupMasterQQ integer(8),createDate datetime,teamSum integer(1))");
                    statement.executeUpdate("CREATE TABLE if not exists teamMember(userQQ integer(8) PRIMARY KEY,groupQQ integer(8),name varchar(20),power boolean,token varchar(20))");
                    statement.executeUpdate("CREATE TABLE if not exists knifeList(id integer PRIMARY KEY AUTOINCREMENT, knifeQQ integer(8), hurt integer,date datetime,complete boolean, version integer ,loop integer(1),position integer(1) )");
                    statement.executeUpdate("CREATE TABLE if not exists tree(teamQQ integer(8) PRIMARY KEY,date datetime, isTree boolean,groupQQ integer(8) );");
                    statement.executeUpdate("CREATE TABLE if not exists progress(teamQQ integer(8) PRIMARY KEY,loop integer(2) ,  serial integer(1), Remnant integer(8), startTime datetime,endTime datetime,deleted integer(1) ,version integer)");
                    statement.executeUpdate("CREATE TABLE if not exists Scores(QQ integer PRIMARY KEY ,   iSign boolean default false,  score integer(8) default 0)");
                    statement.executeUpdate("create table if not exists 'version'( 'version' integer)");
                    statement.executeUpdate("insert into version values (" + new_version + ")");
                    break;
                case 1:
            }
            statement.executeUpdate("update version set version =" + new_version);
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
