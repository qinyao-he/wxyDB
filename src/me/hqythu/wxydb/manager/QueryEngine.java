package me.hqythu.wxydb.manager;

import me.hqythu.wxydb.exception.level1.SQLQueryException;
import me.hqythu.wxydb.exception.level2.SQLWhereException;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.util.Func;
import me.hqythu.wxydb.util.SelectOption;
import me.hqythu.wxydb.util.Where;

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
     */

    protected double func(SelectOption option, Where where) throws SQLQueryException {
        String tableName;
        String columnName;
        Table table;
        List<Object[]> records;
        Func func;

        func = option.func;
        try {
            tableName = option.tableNames.get(0);
            columnName = option.columnNames.get(0);
            table = SystemManager.getInstance().getTable(tableName);
            records = table.getAllRecords();
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage());
        }

        int col = table.getColumnCol(columnName);
        Integer temp;
        double result = 0;
        int size;
        try {
            switch (func) {
                case SUM:
                    result = 0;
                    for (Object[] record : records) {
                        if (where.match(record, table) && record[col] != null) {
                            result += (Integer) record[col];
                        }
                    }
                    break;
                case AVG:
                    result = 0;
                    size = 0;
                    for (Object[] record : records) {
                        if (where.match(record, table) && record[col] != null) {
                            size++;
                            result += (Integer) record[col];
                        }
                    }
                    if (size == 0) result = 0;
                    else result /= size;
                    break;
                case MAX:
                    result = Double.MIN_VALUE;
                    for (Object[] record : records) {
                        if (where.match(record, table) && record[col] != null) {
                            temp = (Integer) record[col];
                            if (result < temp) result = temp;
                        }
                    }
                    if (records.size() == 0) result = 0;
                    break;
                case MIN:
                    result = Double.MAX_VALUE;
                    for (Object[] record : records) {
                        if (where.match(record, table) && record[col] != null) {
                            temp = (Integer) record[col];
                            if (result > temp) result = temp;
                        }
                    }
                    if (records.size() == 0) result = 0;
                    break;
                case COUNT:
                    result = 0;
                    for (Object[] record : records) {
                        if (where.match(record, table) && record[col] != null) {
                            result++;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage());
        }
        return result;
    }

    /**
     * 表的联合
     * 内部使用
     */


    //------------------------test------------------------
    public List<Map<Table, Integer>> tableJoinRecordIds(Set<String> tableNames) throws SQLQueryException {
        Table table;
        List<Integer> tempIds;

        Map<String, Table> tables = SystemManager.getInstance().getTables();
        List<Table> tableChoose = new ArrayList<>(tableNames.size());

        try {
            for (String tableName : tableNames) {
                table = tables.get(tableName);
                tableChoose.add(table);
            }
        } catch (Exception e) {
            throw new SQLQueryException("get tables error, may have not open database");
        }

        try {
            List<Map<Table, Integer>> results;
            List<Map<Table, Integer>> tempResults;

            // 第一个表
            table = tableChoose.get(0);
            tempIds = table.getAllRecordIds();
//            results = new LinkedList<>();
            results = new ArrayList<>(tempIds.size());
            for (Integer tempId : tempIds) {
                Map<Table, Integer> record = new HashMap<>();
                record.put(table, tempId);
                results.add(record);
            }

            for (int i = 1; i < tableChoose.size(); i++) {
                table = tableChoose.get(i);

                tempIds = table.getAllRecordIds();
                tempResults = new ArrayList<>(results.size() * tempIds.size());

                // 每条记录
                for (Integer tempRecord : tempIds) {

                    // 累积至上一次的结果
                    for (Map<Table, Integer> result : results) {
                        Map<Table, Integer> record = new HashMap<>(results.size() + 1);
                        for (Map.Entry<Table, Integer> entry : result.entrySet()) {
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
            throw new SQLQueryException(e.getMessage());
        }
    }

    /**
     *
     */
    public List<Object[]> queryById(SelectOption select, Where where) throws SQLQueryException {
        List<Object[]> result;

        // Func类型
        if (select.isFunc()) {
            result = new ArrayList<>();
            Object[] ret = new Object[1];
            ret[0] = func(select, where);
            result.add(ret);
            return result;
        }

        Map<String, Table> tables;
        List<Map<Table, Integer>> tempIds;

        Table[] t; // select对应的表
        int[] c;   // select对应的列号
        int nCol;
        try {
            tables = SystemManager.getInstance().getTables();
            tempIds = tableJoinRecordIds(select.fromTableNames);
            if (select.isAll()) {
                nCol = 0;
                for (String tableName : select.getFromTableNames()) {
                    Table table = tables.get(tableName);
                    nCol += table.getColumns().length;
                }
                t = new Table[nCol];
                c = new int[nCol];
            } else {
                nCol = select.tableNames.size();    // 查询结果的列数
                t = new Table[nCol];            // select对应的表
                c = new int[nCol];                // select对应的列号
                for (int i = 0; i < t.length; i++) {
                    t[i] = tables.get(select.tableNames.get(i));
                    c[i] = t[i].getColumnCol(select.columnNames.get(i));
                }
            }
        } catch (Exception e) {
            throw new SQLQueryException("query error : " + e.getMessage());
        }

        result = new ArrayList<>(tempIds.size());
        try {
            for (Map<Table, Integer> tempId : tempIds) {
                Map<Table, Object[]> temp = new HashMap<>();
                for (Map.Entry<Table, Integer> id : tempId.entrySet()) {
                    Object[] o = id.getKey().getRecord(id.getValue());
                    temp.put(id.getKey(), o);
                }
                if (where.match(temp, tables)) {
                    Object[] record = new Object[nCol];
                    if (select.isAll()) {
                        int ii = 0;
                        for (Object[] objects : temp.values()) {
                            for (int i = 0; i < objects.length; i++, ii++) {
                                record[ii] = objects[i];
                            }
                        }
                    } else {
                        for (int i = 0; i < nCol; i++) {
                            record[i] = temp.get(t[i])[c[i]];
                        }
                    }
                    result.add(record);
                }
            }
        } catch (Exception e) {
            throw new SQLQueryException("query error : " + e.getMessage());
        }

        return result;
    }

    public static String resultsToString(List<Object[]> results) {
        StringBuilder builder = new StringBuilder();
        if (results == null) {
            builder.append("result is null");
        } else if (results.isEmpty()) {
            builder.append("empty");
        } else {
            for (Object[] result : results) {
                builder.append(Arrays.toString(result));
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    protected List<Object[]> query(SelectOption select, Where where) throws SQLQueryException {
        List<Object[]> result;
        Table[] t; // select对应的表
        int[] c;   // select对应的列号
        int nCol;

        // Func类型
        if (select.isFunc()) {
            result = new ArrayList<>();
            Object[] ret = new Object[1];
            ret[0] = func(select, where);
            result.add(ret);
            return result;
        }

        // 初始化查询结果相关信息
        Map<String, Table> tables;
        List<Map<Table, Object[]>> temps;
        try {
            tables = SystemManager.getInstance().getTables();
            temps = tableJoinRecords(select.fromTableNames);
            if (select.isAll()) {
                nCol = 0;
                for (String tableName : select.getFromTableNames()) {
                    Table table = tables.get(tableName);
                    nCol += table.getColumns().length;
                }
                t = new Table[nCol];
                c = new int[nCol];
            } else {
                nCol = select.tableNames.size();    // 查询结果的列数
                t = new Table[nCol];            // select对应的表
                c = new int[nCol];                // select对应的列号
                for (int i = 0; i < t.length; i++) {
                    t[i] = tables.get(select.tableNames.get(i));
                    c[i] = t[i].getColumnCol(select.columnNames.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLQueryException(e.getMessage());
        }

        // 执行查询
        try {
            result = new ArrayList<>(temps.size());
            for (Map<Table, Object[]> temp : temps) {
                if (where.match(temp, tables)) {
                    Object[] record = new Object[nCol];
                    if (select.isAll()) { // select *
                        int ii = 0;
                        for (Object[] objects : temp.values()) {
                            for (int i = 0; i < objects.length; i++, ii++) {
                                record[ii] = objects[i];
                            }
                        }
                    } else { // 相应的列
                        for (int i = 0; i < nCol; i++) {
                            record[i] = temp.get(t[i])[c[i]];
                        }
                    }
                    result.add(record);
                }
            }
        } catch (Exception e) {
            throw new SQLQueryException("query error : " + e.getMessage());
        }

        return result;
    }

    protected List<Map<Table, Object[]>> tableJoinRecords(Set<String> tableNames) throws SQLQueryException {
        Table table;
        List<Object[]> tempRecords;

        Map<String, Table> tables = SystemManager.getInstance().getTables();
        List<Table> tableChoose = new ArrayList<>(tableNames.size());
        try {
            for (String tableName : tableNames) {
                table = tables.get(tableName);
                tableChoose.add(table);
            }
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
            throw new SQLQueryException(e.getMessage());
        }
    }
}
