package me.hqythu.manager;

import me.hqythu.exception.SQLQueryException;
import me.hqythu.exception.SQLWhereException;
import me.hqythu.object.Column;
import me.hqythu.object.Table;
import me.hqythu.util.SeleteOption;
import me.hqythu.util.Where;

import java.util.*;

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

    /**
     * 查询
     * (未完成)
     */
    public List<Object[]> query(SeleteOption select, Where where) throws SQLQueryException {
        Map<String, Table> tables = SystemManager.getInstance().getTables();
        List<String> tableNames = where.getTableNames();
        List<Map<Table, Object[]>> temps = tableJoinRecords(tableNames);
        List<Object[]> result = new ArrayList<>(temps.size());

        try {
//            Table[] t = new Table[select.tableNames.size()];
//            int[] c = new int[select.columnNames.size()];
            for (Map<Table, Object[]> temp : temps) {
                if (where.match(temp, tables)) {
//                    temp.get(t)
//                    result.add()\
                }
            }
        } catch (SQLWhereException e) {
            e.printStackTrace();
            throw new SQLQueryException("query error : "+e.getMessage());
        }

        return null;
    }

    /**
     * 表的联合
     * 未检查tableNames的重复
     *
     * @param tableNames
     * @return
     */
    public List<Map<Table, Object[]>> tableJoinRecords(List<String> tableNames) {
        Table table;
        List<Object[]> tempRecords;

        Map<String, Table> tables = SystemManager.getInstance().getTables();
        List<Table> tableChoose = new ArrayList<>(tableNames.size());
        for (String tableName : tableNames) {
            table = tables.get(tableName);
            tableChoose.add(table);
        }

        try {
            List<Map<Table, Object[]>> results;
            List<Map<Table, Object[]>> tempResults;

            // 第一个表
            table = tableChoose.get(0);
            tempRecords = table.getAllRecords();
//            results = new LinkedList<>();
            results = new ArrayList<>(tempRecords.size());
            for (Object[] tempRecord : tempRecords) {
                Map<Table, Object[]> record = new HashMap<>();
                record.put(table, tempRecord);
                results.add(record);
            }

            for (int i = 1; i < tableChoose.size(); i++) {
                table = tableChoose.get(i);

                tempRecords = table.getAllRecords();
//                tempResults = new LinkedList<>();
                tempResults = new ArrayList<>(results.size() * tempRecords.size());

                // 每条记录
                for (Object[] tempRecord : tempRecords) {

                    // 累积至上一次的结果
                    for (Map<Table, Object[]> result : results) {
                        Map<Table, Object[]> record = new HashMap<>(results.size() + 1);
                        for (Map.Entry<Table, Object[]> entry : result.entrySet()) {
                            record.put(entry.getKey(), entry.getValue());
                        }
                        record.put(table, tempRecord);
                        tempResults.add(record);
                    }
                }

                results = tempResults;
            }
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
