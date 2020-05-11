package cn.sciuridae.DB.sqLite;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    T mapRow(ResultSet rs, int index) throws SQLException;
}