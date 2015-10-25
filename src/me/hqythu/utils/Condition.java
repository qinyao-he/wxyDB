package me.hqythu.utils;

/**
 * 条件
 */
public class Condition<T> {
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String EQU = "equ";
    public static final String NEQ = "neq";
    public static final String LES = "les";
    public static final String LEQ = "leq";
    public static final String GTR = "gtr";
    public static final String GEQ = "geq";

    public Condition() {

    }

    public boolean match(T obj) {
        return false;
    }

}
