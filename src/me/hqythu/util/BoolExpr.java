package me.hqythu.util;

import me.hqythu.exception.SQLWhereException;

/**
 * BoolExpr boolean表达式
 * 左操作数 操作 右操作数
 * 如果tableName不为null,则认为value是变量,需要赋值才能计算boolean
 * 左操作数一定是变量 操作是CalcOp 右操作数可能是变量,可能是常量
 */
public class BoolExpr {

    public String tableNameL;       // 解析得到(必须有)
    public String columnNameL;      // 解析得到
    public Object valueL;           // 解析得到

    public CompareOp compareOp;     // 解析得到(必须有)

    public String tableNameR;       // 解析得到(必须有)
    public String columnNameR;      // 解析得到
    public Object valueR;           // 解析得到

    // 0==0, true
    public BoolExpr() {
        tableNameL = null; // null表示该值为常量
        valueL = 0;
        compareOp = CompareOp.EQU;
        tableNameR = null; // null表示该值为常量
        valueR = 0;
    }
    
    // 常用的表达式
    // if left : var op const   变量在左边
    // else : const op var      变量在右边
    public BoolExpr(String tableName, String columnName, CompareOp op, Object value, boolean left) {
        if (left) {
            tableNameL = tableName;
            columnNameL = columnName;
            tableNameR = null;
            valueR = value;
        } else {
            tableNameL = null;
            valueL = value;
            tableNameR = tableName;
            columnNameR = columnName;
        }
        compareOp = op;
    }


    //--------------------删除,更新,查询--------------------
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
