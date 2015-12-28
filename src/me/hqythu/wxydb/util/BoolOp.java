package me.hqythu.wxydb.util;

public enum BoolOp {
    AND, OR;
    public String toString() {
        switch (this) {
            case AND:
                return "and";
            case OR:
                return "or";
        }
        return "boolop";
    }
}
