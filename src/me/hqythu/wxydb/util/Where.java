package me.hqythu.wxydb.util;

import me.hqythu.wxydb.exception.SQLWhereException;
import me.hqythu.wxydb.object.Table;

import java.util.*;

/**
 * where条件
 */
public class Where {

    // SQL解析得到boolExprs和boolOps
    // 如果不考虑NOT,则boolExpr的个数一定比boolOps个数多1

    public List<Boolean> isExprs; // 这个容易漏!!!
    public List<Object> boolExprsAndOps;

    public Stack<Boolean> forCalc; // 求值用,SQL不用管

    public Where() {
        isExprs = new ArrayList<>();
        boolExprsAndOps = new ArrayList<>();
        forCalc = new Stack<>();
    }

    //-------------------外部调用-------------------

    public void clear() {
        isExprs.clear();
        boolExprsAndOps.clear();
    }

    public boolean match(Map<Table, Object[]> records, Map<String, Table> tables) throws SQLWhereException {
        Table table;
        Object[] record;
        int col;

//        forCalc.clear();
        for (int i = 0; i < isExprs.size(); i++) {
            if (isExprs.get(i)) {
                BoolExpr boolExpr = (BoolExpr) boolExprsAndOps.get(i);
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
                forCalc.push(boolExpr.getResult());
            } else {
                BoolOp boolOp = (BoolOp) boolExprsAndOps.get(i);
                Boolean bools1 = forCalc.pop();
                Boolean bools2 = forCalc.pop();
                Boolean boold = false;
                switch (boolOp) {
                    case AND:
                        boold = bools1 && bools2;
                        break;
                    case OR:
                        boold = bools1 || bools2;
                        break;
                }
                forCalc.push(boold);
            }
        }
        return forCalc.pop();
    }

}
