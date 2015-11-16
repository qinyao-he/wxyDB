package me.hqythu.PageFile;

import me.hqythu.Global;
import me.hqythu.system.Table;

import java.nio.ByteBuffer;
import java.util.Map;

public class DbPageUser {
    // for BitSet
    private static final byte[] aSetMask = {
            (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
    };
    private static final byte[] aClearMask = {
            (byte) 0x7f, (byte) 0xbf, (byte) 0xdf, (byte) 0xef,
            (byte) 0xf7, (byte) 0xfb, (byte) 0xfd, (byte) 0xfe
    };

    private DbPageUser() {
    }

    public static void initDbPage(Page page) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DBPAGE_INFO_POS + 0, 0); // 初始化表的个数
        buffer.position(Global.DBPAGE_BITMAP_POS);    // 初始化页bitmap
        for (int i = 0; i < Global.DBPAGE_BITMAP_LEN; i++) {
            buffer.put((byte) 0);
        }
        buffer.put(Global.DBPAGE_BITMAP_POS, (byte) 0x80); // 第一页标记1，已被使用
        page.setDirty();
    }

    public static void initTableFromPage(Page dbPage, Map<String, Table> tables) {
        int fileId = dbPage.getFileId();
        byte[] dbPageData = dbPage.getData();
        ByteBuffer dbPageBuffer = dbPage.getBuffer();
        int nTable = dbPageBuffer.getInt(Global.DBPAGE_INFO_POS);

        // 初始化表信息
        try {
            for (int i = 0; i < nTable; i++) {
                int offset = Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN;
                int pageIndex = dbPageBuffer.getInt(offset);
                String name = new String(dbPageData, offset + 4, Global.PER_TABLE_INFO_LEN - 4);
                Page page = BufPageManager.getInstance().getPage(fileId, pageIndex);
                Table table = TablePage.readTablePage(page);
                tables.put(name, table);
            }
            dbPage.setDirty();
        } catch (Exception e) {
            tables.clear();
            e.printStackTrace();
        }
    }

    /**
     * 添加表的信息
     */
    public static int addTableInfo(Page dbPage, String tableName) {

        int tablePageIndex = nextClearBit(dbPage);
        setBitMap(dbPage, tablePageIndex);

        ByteBuffer dbPageBuffer = dbPage.getBuffer();
        int nTable = dbPageBuffer.getInt(Global.DBPAGE_INFO_POS);
        int pos = Global.DBPAGE_TABLE_POS + nTable * Global.PER_TABLE_INFO_LEN;

        dbPageBuffer.putInt(pos, tablePageIndex);        // 表的位置
        dbPageBuffer.position(pos + 4);
        dbPageBuffer.put(tableName.getBytes()); // 未检查表名长度是否小于128
        dbPageBuffer.put((byte) 0);
        dbPageBuffer.putInt(Global.DBPAGE_INFO_POS, nTable + 1);
        dbPage.setDirty();

        return tablePageIndex;
    }

    /**
     * 删除表的信息
     */
    public static void delTableInfo(Page dbPage, String tableName) {
        byte[] dbPageData = dbPage.getData();
        ByteBuffer dbPageBuffer = dbPage.getBuffer();

        int size = dbPageBuffer.getInt(Global.DBPAGE_INFO_POS);
        int i; // 被删除的表信息在哪一行
        for (i = 0; i < size; i++) {
            int offset = Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN;
            int pageIndex = dbPageBuffer.getInt(offset);
            String name = new String(dbPageData, offset + 4, Global.PER_TABLE_INFO_LEN - 4);
            if (name.equals(tableName)) { // 删除表信息
                clearBitMap(dbPage, pageIndex);
                break;
            }
        }
        System.arraycopy(dbPageData,
                Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN + 128,
                dbPageData,
                Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN,
                (size - i - 1) * Global.PER_TABLE_INFO_LEN);
        dbPage.setDirty();
    }

    private static void setBitMap(Page dbPage, int index) {
        if (index < 0) throw new RuntimeException("illegal page bitmap index");
        int pos = Global.DBPAGE_BITMAP_POS + (int) (index / 8);
        try {

            byte[] dbPageData = dbPage.getData();
            dbPageData[pos] |= aSetMask[index % 8];
            dbPage.setDirty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearBitMap(Page dbPage, int index) {
        if (index < 0) throw new RuntimeException("illegal page bitmap index");
        int pos = Global.DBPAGE_BITMAP_POS + (int) (index / 8);
        try {
            byte[] dbPageData = dbPage.getData();
            dbPageData[pos] &= aClearMask[index % 8];
            dbPage.setDirty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int nextClearBit(Page dbPage) {
        try {
            byte[] dbPageData = dbPage.getData();
            for (int i = Global.DBPAGE_BITMAP_POS; i < Global.DBPAGE_TABLE_POS; i++) {
                byte b = dbPageData[i];
                for (int j = 0; j < 8; j++) {
                    if ((b & aSetMask[j]) == 0) return (i - Global.DBPAGE_BITMAP_POS) * 8 + j;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
