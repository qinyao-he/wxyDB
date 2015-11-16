package me.hqythu.util;

/**
 * 列
 */
public class Column {
    public Column(String name, DataType type, short len) {
        this.name = name;
        this.prop = 0;
        this.type = type;
        this.len = len;
    }
    public Column(String name, int prop, DataType type, short len) {
        this.name = name;
        this.prop = prop;
        this.type = type;
        this.len = len;
    }

    public void setNull() {
        prop |= 0x80000000;
    }
    public void clearNull() {
        prop &= 0x7fffffff;
    }
    public boolean isNull() {
        return (prop & 0x80000000) != 0;
    }

    public String name;
    public int prop;
    public DataType type;
    public short len;
}
