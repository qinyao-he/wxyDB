package me.hqythu.util;

public class BitSetMask {

    // for BitSet
    public static final byte[] SET_MASK = {
            (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
    };

    /**
     * 设置位图
     * @param data byte字节数组
     * @param offset 偏移字节数
     * @param index 设置的位
     */
    public static void setBit(byte[] data, int offset, int index) {
        int pos = offset + index/8;
        data[pos] |= SET_MASK[index % 8];
    }
    public static void clearBit(byte[] data, int offset, int index) {
        int pos = offset + index/8;
        data[pos] &= (byte)~SET_MASK[index % 8];
    }
    public static int nextClearBit(byte[] data, int offset, int maxOffset) {
        for (int i = offset; i < maxOffset; i++) {
            byte b = data[i];
            for (int j = 0; j < 8; j++) {
                if ((b & SET_MASK[j]) == 0) return (i - offset) * 8 + j;
            }
        }
        return -1;
    }
    public static boolean checkBit(byte[] data, int offset, int index) {
        int pos = offset + index/8;
        byte b = data[pos];
        return (b & SET_MASK[index % 8]) != 0;
    }
}
