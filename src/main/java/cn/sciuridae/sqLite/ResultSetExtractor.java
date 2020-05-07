package cn.sciuridae.sqLite;

import java.sql.ResultSet;

public interface ResultSetExtractor<T> {

    T extractData(ResultSet rs);

}