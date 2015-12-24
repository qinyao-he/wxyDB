package me.hqythu.manager;

import me.hqythu.exception.SQLExecException;
import me.hqythu.object.Table;
import me.hqythu.object.QuerySet;
import me.hqythu.pagefile.Page;
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

    public QuerySet query(SelectOption option, Where where) throws SQLExecException {

//        Page dbPage = SystemManager.getInstance().getDbPage();
//        int recordLen = 0;
//        String tableNames[] = option.tableNames;
//        for (int i = 0; i < tableNames.length; i++) {
//            Table table = SystemManager.getInstance().getTable(tableNames[i]);
//            for (int j = 0; j < 1; j++) {
//                recordLen += 1;
//            }
//        }

        return null;
    }

}
