package me.hqythu.sql;

import me.hqythu.exception.SQLExecException;
import me.hqythu.manager.SystemManager;
import me.hqythu.object.Table;
import me.hqythu.util.SelectOption;
import me.hqythu.util.Where;

/**
 * QueryEngine
 * 查询器
 * 记录的查询
 * 记录查询的优化、工作计划
 */
public class QueryEngine {
    private static QueryEngine engine = null;

    public static QueryEngine getInstance() {
        if (engine == null) {
            engine = new QueryEngine();
        }
        return engine;
    }

    public QuerySet query(String tableName, String[] fields, SelectOption option, Where where) throws SQLExecException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLExecException("not have table: " + tableName);
        return null;
//        return table.query(fields, option, where);
    }

    public QuerySet query(String tableName, int[] cols, SelectOption option, Where where) throws SQLExecException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLExecException("not have table: " + tableName);
        return null;
//        return table.query(cols,option,where);
    }

}
