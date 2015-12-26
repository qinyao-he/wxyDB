package me.hqythu.object;

/**
 * 列
 */
public class Column {

    public static final int FLAG_NOTNULL = 0x80000000;
    public static final int FLAG_PRIMARY = 0x40000000;
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
    public void setLength(short len) {
        this.len = len;
    }
    public void setNotNull() {
        prop |= FLAG_NOTNULL;
    }
    public void clearNotNull() {
        prop &= ~FLAG_NOTNULL;
    }
    public boolean notNull() {
        return (prop & FLAG_NOTNULL) != 0;
    }
    public void setPrimary() {
        prop |= FLAG_PRIMARY;
    }
    public void clearPrimary() {
        prop &= ~FLAG_PRIMARY;
    }
    public boolean isPrimary() {
        return (prop & FLAG_PRIMARY) != 0;
    }

    // 列名
    public String name;
    // 列数据类型
    public DataType type;
    // 列长度
    public short len;
    // 该列属性,如是否为null
    public int prop;

    public String toString() {
        StringBuilder builder = new StringBuilder(1024);
        builder.append(name);
        builder.append('(');
        switch (type) {
            case UNKNOWN:
                builder.append("unkown");
                break;
            case INT:
                builder.append("int");
                break;
            case VARCHAR:
                builder.append("varchar(");
                builder.append(len);
                builder.append(')');
                break;
        }
        builder.append(')');
        if (notNull()) {
            builder.append(" not null");
        }
        if (isPrimary()) {
            builder.append(" primary key");
        }
        return builder.toString();
    }
}
