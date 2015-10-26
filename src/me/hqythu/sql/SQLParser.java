package me.hqythu.sql;

import me.hqythu.exception.SQLParserException;

public class SQLParser {
    private SQLParser() {}

    /**
     * 解析SQL
     * 输入一个字符串，返回一个SQL
     * @param sql
     * @return
     * @throws SQLParserException
     */
    public static SQL parse(String sql) throws SQLParserException {
        SQL s = new SQL();
        // parse sql 得到 SQL
        return s;
    }
}
