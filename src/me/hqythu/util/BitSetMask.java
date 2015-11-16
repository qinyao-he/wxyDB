package me.hqythu.util;

public class BitSetMask {

    // for BitSet
    public static final byte[] aSetMask = {
            (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
    };
    public static final byte[] aClearMask = {
            (byte) 0x7f, (byte) 0xbf, (byte) 0xdf, (byte) 0xef,
            (byte) 0xf7, (byte) 0xfb, (byte) 0xfd, (byte) 0xfe
    };

    public static void setBit(byte[] data, int offset, int index) {
        int pos = offset + index/8;
        data[pos] |= aSetMask[index % 8];
    }
    public static void clearBit(byte[] data, int offset, int index) {
        int pos = offset + index/8;
        data[pos] &= aClearMask[index % 8];
    }
    public static int nextClearBit(byte[] data, int offset, int maxOffset) {
        for (int i = offset; i < maxOffset; i++) {
            byte b = data[i];
            for (int j = 0; j < 8; j++) {
                if ((b & aSetMask[j]) == 0) return (i - offset) * 8 + j;
            }
        }
        return -1;
    }
}
