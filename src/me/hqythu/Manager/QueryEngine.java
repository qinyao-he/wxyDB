package me.hqythu.manager;

import me.hqythu.exception.SQLExecException;
import me.hqythu.object.Table;
import me.hqythu.object.QuerySet;
import me.hqythu.pagefile.Page;
import me.hqythu.util.SelectOption;
import me.hqythu.util.Where;

import java.util.List;
import java.util.Map;

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

    public List<Object[]> query(Where where) throws SQLExecException {
        Map<String,Table> tables = SystemManager.getInstance().getTables();

        return null;
    }

    public String queryResultToString(List<Object[]> results) {
        return null;
    }

}
