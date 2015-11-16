package me.hqythu.util;

public enum DataType {
    UNKNOWN, INT, VARCHAR;

    public static DataType valueOf(int val) {
        switch (val) {
            case 1:
                return INT;
            case 2:
                return VARCHAR;
            default:
                return UNKNOWN;
        }
    }
}
