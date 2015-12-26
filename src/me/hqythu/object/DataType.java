package me.hqythu.object;

/**
 * 列数据类型
 */
public enum DataType {
    UNKNOWN, INT, VARCHAR;

    public static DataType valueOf(short val) {
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
