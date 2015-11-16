package me.hqythu.util;

import me.hqythu.util.DataType;

public class Column { // 列
    public Column(String name, DataType type, int len) {
        this.name = name;
        this.prop = 0;
        this.type = type;
        this.len = len;
    }
    public Column(String name, int prop, DataType type, int len) {
        this.name = name;
        this.prop = prop;
        this.type = type;
        this.len = len;
    }
    public String name;
    public int prop;
    public DataType type;
    public int len;
}
