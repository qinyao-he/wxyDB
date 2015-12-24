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
    public static byte[] valueToByte(Column[] columns, int recordLen, int[] cols, Object[] values) throws SQLRecordException {
//        System.out.println(recordLen);
//        ByteBuffer buffer = ByteBuffer.allocateDirect(recordLen);
        ByteBuffer buffer = ByteBuffer.allocate(recordLen);

        System.out.println(buffer.capacity());
        int[] offsets = new int[columns.length+1];
        offsets[0] = 0;
        for (int i = 0; i < columns.length; i++) {
            offsets[i+1] = offsets[i] + columns[i].len;
        }

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
                if (!column.isNull()) {
                    throw new SQLRecordException("not null try to null");
                } else {
                    BitSetMask.setBit(buffer.array(), recordLen, i);
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

}
