package me.hqythu.util;

import me.hqythu.object.Column;

/**
 * where条件
 */
public class Where {
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String EQU = "equ";
    public static final String NEQ = "neq";
    public static final String LES = "les";
    public static final String LEQ = "leq";
    public static final String GTR = "gtr";
    public static final String GEQ = "geq";

    enum Op {
        AND,OR,EQU,NEQ,LES,LEQ,GTR,GEO
    }

    private BoolExpr boolExpr;

    private Op op;

    public Where() {

    }

    public boolean match(byte[] record, Column[] columns) {
        return false;
    }

    /**
     * 优化查询
     */
    public void optimize() {

    }

    public void setOp(Op op) {
        this.op = op;
    }
}
