package me.hqythu.util;

import me.hqythu.exception.SQLWhereException;
import me.hqythu.object.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * where条件
 */
public class Where {

    // SQL解析得到boolExprs和boolOps
    // 如果不考虑NOT,则boolExpr的个数一定比boolOps个数多1

    // 所需的表
    // 查询的时候,需要知道涉及的表,根据所需的表,传入相应的参数
    public List<String> tableNames;

    public List<BoolExpr> boolExprs;
    public List<BoolOp> boolOps;

    public Where() {
        tableNames = new ArrayList<>();
        boolExprs = new ArrayList<>();
        boolOps = new ArrayList<>();
    }

    //-------------------外部置参和使用-------------------
    public void setValues(Map<Table,Object[]> records, Map<String,Table> tables) throws SQLWhereException {
        Table table;
        Object[] record;
        int col;
        try {
            for (BoolExpr boolExpr : boolExprs) {
                if (boolExpr.isNeedValueL()) {
                    table = tables.get(boolExpr.tableNameL);
                    record = records.get(table);
                    col = table.getColumnCol(boolExpr.columnNameL);
                    boolExpr.setValueL(record[col]);
                }
                if (boolExpr.isNeedValueR()) {
                    table = tables.get(boolExpr.tableNameR);
                    record = records.get(table);
                    col = table.getColumnCol(boolExpr.columnNameR);
                    boolExpr.setValueR(record[col]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLWhereException("where error : set value failed "+e.getMessage());
        }
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void clear() {
        boolExprs.clear();
        boolOps.clear();
    }

    public boolean match(Map<Table,Object[]> records, Map<String,Table> tables) throws SQLWhereException {
        setValues(records, tables);
        boolean temp = boolExprs.get(0).getResult();
        for (int i = 1; i < boolExprs.size(); i++) {
            switch (boolOps.get(i-1)) {
                case AND:
                    temp = temp && boolExprs.get(i).getResult();
                    break;
                case OR:
                    temp = temp || boolExprs.get(i).getResult();
                    break;
            }
        }
        return temp;
    }


}
