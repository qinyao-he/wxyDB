package me.hqythu.wxydb.util;

/**
 * Created by apple on 15/12/27.
 */
public enum Func {
    SUM, AVG, MAX, MIN, COUNT;

    public String toString() {
        switch (this) {
            case SUM:
                return "sum";
            case AVG:
                return "avg";
            case MAX:
                return "max";
            case MIN:
                return "min";
            case COUNT:
                return "count";
        }
        return "";
    }
}
