package me.hqythu.wxydb.util;

public enum CompareOp {
    EQU, NEQ, LES, LEQ, GTR, GEQ, IS;

    public String toString() {
        switch (this) {
            case EQU:
                return "==";
            case NEQ:
                return "!=";
            case LES:
                return "<";
            case LEQ:
                return "<=";
            case GTR:
                return ">";
            case GEQ:
                return ">=";
            case IS:
                return "is";
        }
        return "op";
    }
}
