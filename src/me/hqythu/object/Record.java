package me.hqythu.object;

import me.hqythu.exception.SQLRecordException;
import me.hqythu.util.BitSetMask;

import java.nio.ByteBuffer;

import static java.lang.Math.ceil;

public class Record {
    private Record(){}

    /**
     * 将内容转为byte[]
     */
    public static byte[] valueToByte(Column[] columns, short recordLen, int[] cols, Object[] values) throws SQLRecordException {

        ByteBuffer buffer = ByteBuffer.allocateDirect(recordLen);
        int[] offsets = new int[columns.length];
        offsets[0] = 0;
        for (int i = 1; i < columns.length; i++) {
            offsets[i] = offsets[i-1] + columns[i-1].len;
        }

        // 状态位AB
        buffer.putShort((short)0);
        // 列数
        buffer.putShort((short)columns.length);
        // 定长部分长度
        buffer.putShort(recordLen);
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
                        buffer.putInt(offsets[cols[i]]+6,ii);
                        BitSetMask.clearBit(buffer.array(), recordLen, i);
                        break;
                    case VARCHAR:
                        String ss = (String) values[i];
                        if (ss.length() > column.len) {
                            throw new SQLRecordException("column data too long");
                        }
                        buffer.position(offsets[cols[i]]+6);
                        buffer.put(ss.getBytes());
                        BitSetMask.clearBit(buffer.array(), recordLen, i);
                        break;
                }
            }
        }
        return buffer.array();
    }

    /**
     * 由该列组成的记录的长度
     */
    public static short getRecordLen(Column[] columns) {
        short len = 0;
        for (Column column : columns) {
            len += column.len;
        }
        len += 1 + 1 + 2 + 2 + ceil((double)len/8);
        return len;
    }

}
