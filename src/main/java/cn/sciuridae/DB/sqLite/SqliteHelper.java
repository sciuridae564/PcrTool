package cn.sciuridae.DB.sqLite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqliteHelper {
    final static Logger logger = LoggerFactory.getLogger(SqliteHelper.class);

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private String dbFilePath;

    /**
     * 构造函数
     * @param dbFilePath sqlite Instance 文件路径
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public SqliteHelper(String dbFilePath) throws ClassNotFoundException, SQLException {
        this.dbFilePath = dbFilePath;
        connection = getConnection(dbFilePath);
    }

    /**
     * 获取数据库连接
     * @param dbFilePath db文件路径
     * @return 数据库连接
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection(String dbFilePath) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        return conn;
    }

    /**
     * 执行sql查询
     * @param sql sql select 语句
     * @param rse 结果集处理类对象
     * @return 查询结果
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public <T> T executeQuery(String sql, ResultSetExtractor<T> rse) throws SQLException, ClassNotFoundException {
        try {
            resultSet = getStatement().executeQuery(sql);
            T rs = rse.extractData(resultSet);
            return rs;
        } finally {
            destroyed();
        }
    }

    /**
     * 执行select查询，返回结果列表
     *
     * @param sql sql select 语句
     * @param rm 结果集的行数据处理类对象
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public <T> List<T> executeQuery(String sql, RowMapper<T> rm) throws SQLException, ClassNotFoundException {
        List<T> rsList = new ArrayList<T>();
        try {
            resultSet = getStatement().executeQuery(sql);
            while (resultSet.next()) {
                rsList.add(rm.mapRow(resultSet, resultSet.getRow()));
            }

        } finally {
            destroyed();
        }
        return rsList;
    }

    /**
     * 执行数据库更新sql语句
     * @param sql
     * @return 更新行数
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        try {
            int c = getStatement().executeUpdate(sql);
            return c;
        } finally {
            destroyed();
        }

    }

    /**
     * 执行多个sql更新语句
     * @param sqls
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeUpdate(String...sqls) throws SQLException, ClassNotFoundException {
        try {
            for (String sql : sqls) {
                getStatement().executeUpdate(sql);
            }
        } finally {
            destroyed();
        }
    }

    /**
     * 执行数据库更新 sql List
     * @param sqls sql列表
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeUpdate(List<String> sqls) throws SQLException, ClassNotFoundException {
        try {
            for (String sql : sqls) {
                getStatement().executeUpdate(sql);
            }
        } finally {
            destroyed();
        }
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (null == connection) connection = getConnection(dbFilePath);
        return connection;
    }

    private Statement getStatement() throws SQLException, ClassNotFoundException {
        if (null == statement) statement = getConnection().createStatement();
        return statement;
    }

    /**
     * 数据库资源关闭和释放
     */
    public void destroyed() {
        try {
            if (null != statement) {
                statement.close();
                statement = null;
            }

            if (null != connection) {
                connection.close();
                connection = null;
            }

            if (null != resultSet) {
                resultSet.close();
                resultSet = null;
            }
        } catch (SQLException e) {
            logger.error("Sqlite数据库关闭时异常", e);
        }
    }

    /**
     * 执行select查询，返回结果列表
     *
     * @param sql sql select 语句
     * @param clazz 实体泛型
     * @return 实体集合
     * @throws SQLException 异常信息
     * @throws ClassNotFoundException 异常信息
     */
    public <T> List<T> executeQueryList(String sql, Class<T> clazz) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<T> rsList = new ArrayList<T>();
        try {
            resultSet = getStatement().executeQuery(sql);
            while (resultSet.next()) {
                T t = clazz.newInstance();
                for (Field field : t.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    field.set(t,resultSet.getObject(field.getName()));
                }
                rsList.add(t);
            }
        } finally {
            destroyed();
        }
        return rsList;
    }

    /**
     * 执行sql查询,适用单条结果集
     * @param sql sql select 语句
     * @param clazz 结果集处理类对象
     * @return 查询结果
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public <T> T executeQuery(String sql, Class<T> clazz) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            resultSet = getStatement().executeQuery(sql);
            T t = clazz.newInstance();
            for (Field field : t.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(t,resultSet.getObject(field.getName()));
            }
            return t;
        } finally {
            destroyed();
        }
    }

    /**
     * 执行数据库更新sql语句
     * @param tableName 表名
     * @param param key-value键值对,key:表中字段名,value:值
     * @return 更新行数
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int executeInsert(String tableName, Map<String,Object> param) throws SQLException, ClassNotFoundException {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO ");
            sql.append(tableName);
            sql.append(" ( ");
            for (String key : param.keySet()) {
                sql.append(key);
                sql.append(",");
            }
            sql.delete(sql.length()-1,sql.length());
            sql.append(")  VALUES ( ");
            for (String key : param.keySet()) {
                sql.append("'");
                sql.append(param.get(key));
                sql.append("',");
            }
            sql.delete(sql.length()-1,sql.length());
            sql.append(");");
            int c = getStatement().executeUpdate(sql.toString());
            return c;
        } finally {
            destroyed();
        }

    }
}