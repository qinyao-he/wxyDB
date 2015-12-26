package me.hqythu.util;

import me.hqythu.exception.SQLWhereException;
import me.hqythu.object.Column;
import me.hqythu.object.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * where条件
 */
public class Where {
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String EQU = "equ"; // ==
    public static final String NEQ = "neq"; // !=
    public static final String LES = "les"; // <
    public static final String LEQ = "leq"; // <=
    public static final String GTR = "gtr"; // >
    public static final String GEQ = "geq"; // >=
    public static final String IS = "is";

    // SQL解析得到boolExprs和boolOps
    // 如果不考虑NOT,则boolExpr的个数一定比boolOps个数多1
    public List<BoolExpr> boolExprs;
    public List<BoolOp> boolOps;

    public Where() {
        List<BoolExpr> boolExprs = new ArrayList<>();
        List<BoolOp> boolOps = new ArrayList<>();
    }

    //-------------------辅助类-------------------
    enum BoolOp {
        AND,OR
    }

    enum CompareOp {
        EQU,NEQ,LES,LEQ,GTR,GEQ,IS
    }

    /**
     * BoolExpr boolean表达式
     * 左操作数 操作 右操作数
     * 如果tableName不为null,则认为value是变量,需要赋值才能计算boolean
     * 左操作数一定是变量 操作是CalcOp 右操作数可能是变量,可能是常量
     */
    public class BoolExpr {
        public String tableNameL = null;        // 解析得到
        public String columnNameL = null;       // 解析得到
        public Object valueL = null;

        public CompareOp compareOp;                   // 解析得到

        public String tableNameR = null;        // 解析得到
        public String columnNameR = null;       // 解析得到
        public Object valueR = null;            // 解析得到

        public boolean isNeedValueL() {return tableNameL != null;}
        public void setValueL(Object value) {valueL = value;}
        public boolean isNeedValueR() {return tableNameR != null;}
        public void setValueR(Object value) {valueR = value;}

        public boolean getResult() throws SQLWhereException {
            // 非 is null 操作结果,如果其中一个为null,则返回false
            if (compareOp != CompareOp.IS) {
                if (valueL == null) return false;
                if (valueR == null) return false;
            }
            try {
                switch (compareOp) {
                    case EQU:
                        return valueL.equals(valueR);
                    case NEQ:
                        return !valueL.equals(valueR);
                    case LES:
                        return (Integer)valueL < (Integer)valueR;
                    case LEQ:
                        return (Integer)valueL <= (Integer)valueR;
                    case GTR:
                        return (Integer)valueL > (Integer)valueR;
                    case GEQ:
                        return (Integer)valueL >= (Integer)valueR;
                    case IS:
                        return valueL == null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLWhereException("where error:"+e.getMessage());
            }
            return false;
        }
    }


    //-------------------外部置参和使用-------------------
    public int getBoolExprSize() {
        return boolExprs.size();
    }
    public int getBoolOpSize() {
        return boolOps.size();
    }
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

    public boolean match(Object[] values) {
        return false;
    }
    public boolean match(byte[] record, Map<String,Table> tables) {
        return false;
    }
    public boolean match(Object[] values, Column[] columns) {
        return false;
    }
    public boolean match(Object[] values, Map<String,Table> tables) {
        return false;
    }

}
