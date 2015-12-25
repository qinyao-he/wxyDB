package me.hqythu.object;

import me.hqythu.exception.SQLRecordException;
import me.hqythu.util.BitSetMask;
import me.hqythu.util.Global;

import java.nio.ByteBuffer;

import static java.lang.Math.ceil;

public class Record {
    private Record(){}

    /**
     * 将内容转为byte[]
     */
    public static byte[] valueToBytes(Table table, int recordLen, int[] cols, Object[] values) throws SQLRecordException {

        ByteBuffer buffer = ByteBuffer.allocate(recordLen);
        Column[] columns = table.getColumns();
        int[] offsets = table.getOffsets();
        int nullPos = offsets[columns.length];

        // 状态位AB
        buffer.clear();
        buffer.putShort((short)0);
        // 列数
        buffer.putShort((short)columns.length);
        // 定长部分长度
        buffer.putShort((short)offsets[columns.length-1]);
        // 定长数据
        for (int i = 0; i < cols.length; i++) {
            Column column = columns[cols[i]];
            if (values[i] == null) {
                if (column.notNull()) {
                    throw new SQLRecordException("not null try to null");
                } else {
                    BitSetMask.setBit(buffer.array(), nullPos, i);
                }
            } else {
                switch (column.type) {
                    case UNKNOWN:
                        throw new SQLRecordException("unknown data type");
                    case INT:
                        Integer ii = (Integer) values[i];
                        buffer.putInt(offsets[cols[i]]+Global.RECORD_STATIC_DATA_POS,ii);
                        break;
                    case VARCHAR:
                        String ss = (String) values[i];
                        if (ss.length() > column.len) {
                            throw new SQLRecordException("column data too long");
                        }
                        buffer.position(offsets[cols[i]]+Global.RECORD_STATIC_DATA_POS);
                        buffer.put(ss.getBytes());
                        break;
                }
                BitSetMask.clearBit(buffer.array(), Global.RECORD_STATIC_DATA_POS + offsets[columns.length], i);
            }
        }
        return buffer.array();
    }

    /**
     * 由该列组成的记录的长度
     */
    public static int getRecordLen(Column[] columns) {
        int len = 0;
        for (Column column : columns) {
            len += column.len;
        }
        len += Global.RECORD_STATUS_LEN + ceil((double)columns.length/8);
        return len;
    }

    public static Object[] bytesToValues(Table table, byte[] record) {
        Column[] columns = table.getColumns();
        int[] offsets = table.getOffsets();
        int nullPos = offsets[columns.length];
        ByteBuffer buffer = ByteBuffer.wrap(record);
        Object[] values = new Object[columns.length];

        for (int i = 0; i < columns.length; i++) {
            // NULL值
            if ((!columns[i].notNull()) && BitSetMask.checkBit(record,nullPos,i)) {
                values[i] = null;
            } else {
                switch (columns[i].type) {
                    case UNKNOWN:
                        values[i] = null;
                        break;
                    case INT:
                        values[i] = buffer.getInt(Global.RECORD_STATIC_DATA_POS+offsets[i]);
                        break;
                    case VARCHAR:
                        String temp = new String(record,Global.RECORD_STATIC_DATA_POS+offsets[i],columns[i].len);
                        if (temp.indexOf(0) > 0) {
                            temp = temp.substring(0,temp.indexOf(0));
                        }
                        values[i] = temp;
                        break;
                }
            }
        }
        return values;
    }

    /**
     * 检查某个列是否为null
     */
    public static boolean checkNull(Table table, byte[] record, int index) {
        int offset = table.getOffsets()[table.getColumns().length];
        return BitSetMask.checkBit(record,offset,index);
    }
}
