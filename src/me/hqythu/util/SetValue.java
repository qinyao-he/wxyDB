package me.hqythu.util;

import me.hqythu.object.Column;
import me.hqythu.object.Table;

import java.util.List;

public class SetValue {

    enum CalcOp {
        ADD,SUB,MUL,DIV
    }

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

    public Object calcValue(Object oldValue) {
        if (calcOp != null) {
            Integer newValue = 0;
            if (isVar1) value1 = oldValue;
            if (isVar2) value2 = oldValue;
            switch (calcOp) {
                case ADD:
                    newValue = (Integer)value1 + (Integer)value2;
                    break;
                case SUB:
                    newValue = (Integer)value1 - (Integer)value2;
                    break;
                case MUL:
                    newValue = (Integer)value1 * (Integer)value2;
                    break;
                case DIV:
                    newValue = (Integer)value1 / (Integer)value2;
                    break;
            }
            return newValue;
        } else {
            return value1;
        }
    }
}
