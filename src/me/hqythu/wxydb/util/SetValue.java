package me.hqythu.wxydb.util;

import java.util.List;

public class SetValue {

    // 认为表达式右边如果有变量,只能为表达式左边这个值
    // 即只支持
    // Table.Column = const; 放在value1
    // Table.Column = Table.Column(const) op Table.Column(const);

    public String columnName;

    public Boolean isVar1 = false;
    public Object value1 = null;
    public CalcOp calcOp = null;
    public Boolean isVar2 = false;
    public Object value2 = null;

    public SetValue() {

    }

    // 赋值常数
    public SetValue(String columnName, Object value) {
        this.columnName = columnName;
        calcOp = null;
        value1 = value;
    }

    // var = var op value
    public SetValue(String columnName, CalcOp op, Object value, boolean left) {
        this.columnName = columnName;
        calcOp = op;
        if (left) {
            isVar1 = true;
            isVar2 = false;
            value2 = value;
        } else {
            isVar1 = false;
            value1 = value;
            isVar2 = true;
        }
    }

    // var = var op var
    public SetValue(String columnName, Object valueL, CalcOp op, Object valueR) {
        this.columnName = columnName;
        calcOp = op;
        isVar1 = true;
        isVar2 = true;
    }

    public Object calcValue(Object oldValue) {
        if (calcOp != null) {
            Integer newValue = 0;
            if (isVar1) value1 = oldValue;
            if (isVar2) value2 = oldValue;
            switch (calcOp) {
                case ADD:
                    newValue = (Integer) value1 + (Integer) value2;
                    break;
                case SUB:
                    newValue = (Integer) value1 - (Integer) value2;
                    break;
                case MUL:
                    newValue = (Integer) value1 * (Integer) value2;
                    break;
                case DIV:
                    newValue = (Integer) value1 / (Integer) value2;
                    break;
            }
            return newValue;
        } else {
            return value1;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("set ");
        builder.append(toExprString());
        return builder.toString();
    }

    public static String toString(List<SetValue> setValues) {
        StringBuilder builder = new StringBuilder();
        builder.append("set ");
        for (int i = 0; i < setValues.size(); i++) {
            builder.append(setValues.get(i).toExprString());
            if (i != setValues.size() - 1) {
                builder.append(',');
            }
        }
        return builder.toString();
    }

    protected String toExprString() {
        StringBuilder builder = new StringBuilder();
        builder.append(columnName);
        if (calcOp == null) {
            builder.append(" = ");
            builder.append(value1);
        } else {
            if (isVar1) {
                builder.append(columnName);
            } else {
                builder.append(value1);
            }
            builder.append(calcOp);
            if (isVar2) {
                builder.append(columnName);
            } else {
                builder.append(value2);
            }
        }
        return builder.toString();
    }
}
