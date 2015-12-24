package me.hqythu.object;

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
    public Column(String name, DataType type, short len, int prop) {
        this.name = name;
        this.prop = prop;
        this.type = type;
        this.len = len;
    }
    public Column() {

    }

    public void setName(String name) {
        this.name = name;
    }
    public void setType(DataType type) {
        this.type = type;
    }
    public void setLen(short len) {
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


    // 列名
    public String name;
    // 列数据类型
    public DataType type;
    // 列长度
    public short len;
    // 该列属性,如是否为null
    public int prop;
}
