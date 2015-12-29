package me.hqythu.wxydb.util;

public enum CalcOp {
    ADD, SUB, MUL, DIV;

    public String toString() {
        String result = null;
        switch (this) {
            case ADD:
                result = "+";
                break;
            case SUB:
                result = "-";
                break;
            case MUL:
                result = "*";
                break;
            case DIV:
                result = "/";
                break;
        }
        return result;
    }
}
