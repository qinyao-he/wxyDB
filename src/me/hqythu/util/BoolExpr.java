package me.hqythu.util;

import me.hqythu.exception.SQLWhereException;

/**
 * BoolExpr boolean表达式
 * 左操作数 操作 右操作数
 * 如果tableName不为null,则认为value是变量,需要赋值才能计算boolean
 * 左操作数一定是变量 操作是CalcOp 右操作数可能是变量,可能是常量
 */
public class BoolExpr {

    enum CompareOp {
        EQU,NEQ,LES,LEQ,GTR,GEQ,IS
    }

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
