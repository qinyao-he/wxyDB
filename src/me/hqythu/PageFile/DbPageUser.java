package me.hqythu.PageFile;

import me.hqythu.util.Global;
import me.hqythu.system.Table;
import me.hqythu.util.BitSetMask;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

public class DbPageUser {

    private DbPageUser() {
    }

    public static void initDbPage(Page page) {
        ByteBuffer buffer = page.getBuffer();

        // 初始化表的个数
        buffer.putInt(Global.DBPAGE_INFO_POS + 0, 0);
        // 初始化页bitmap
        Arrays.fill(buffer.array(), Global.DBPAGE_BITMAP_POS, Global.DBPAGE_TABLE_POS, (byte) 0);

        setBitMap(page, 0); // 第一页标记1，已被使用

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
                Table table = TablePageUser.readTablePage(page);
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
    public static void addTableInfo(Page dbPage, String tableName, int tablePageIndex) {

        ByteBuffer dbPageBuffer = dbPage.getBuffer();
        int nTable = dbPageBuffer.getInt(Global.DBPAGE_INFO_POS);
        int pos = Global.DBPAGE_TABLE_POS + nTable * Global.PER_TABLE_INFO_LEN;

        dbPageBuffer.putInt(pos, tablePageIndex);        // 表的位置
        dbPageBuffer.position(pos + 4);
        dbPageBuffer.put(tableName.getBytes());
        dbPageBuffer.putInt(Global.DBPAGE_INFO_POS, nTable + 1);
        dbPage.setDirty();

    }

    /**
     * 从数据库首页删除表的信息
     */
    public static int delTableInfo(Page dbPage, String tableName) {
        byte[] dbPageData = dbPage.getData();
        ByteBuffer dbPageBuffer = dbPage.getBuffer();
        int pageIndex = -1;
        int size = dbPageBuffer.getInt(Global.DBPAGE_INFO_POS);
        int i; // 被删除的表信息在哪一行
        for (i = 0; i < size; i++) {
            int offset = Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN;
            pageIndex = dbPageBuffer.getInt(offset);
            String name = new String(dbPageData, offset + 4, tableName.length());
            if (name.equals(tableName)) { // 删除表信息
                break;
            }
        }
        // 找到表信息的位置
        // 最后一条表信息移到被删除的位置
        System.arraycopy(dbPageData,
                Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN + 128,
                dbPageData,
                Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN,
                (size - i - 1) * Global.PER_TABLE_INFO_LEN);
        dbPage.setDirty();
        return pageIndex;
    }

    public static Page getNewPage(Page dbPage) {
        int pageId = nextClearBit(dbPage);
        if (pageId < 0 ) return null;
        int fileId = dbPage.getFileId();
        setBitMap(dbPage, pageId);
        try {
            return BufPageManager.getInstance().getPage(fileId,pageId);
        } catch (Exception e) {
            e.printStackTrace();
            clearBitMap(dbPage, pageId);
            return null;
        }
    }

    public static void recyclePage(Page dbPage, int pageId) {
        clearBitMap(dbPage, pageId);
    }

    private static void setBitMap(Page dbPage, int index) {
        if (index < 0) throw new RuntimeException("illegal page bitmap index");
        byte[] dbPageData = dbPage.getData();
        BitSetMask.setBit(dbPageData, Global.DBPAGE_BITMAP_POS, index);
        dbPage.setDirty();
    }

    private static void clearBitMap(Page dbPage, int index) {
        if (index < 0) throw new RuntimeException("illegal page bitmap index");
        byte[] dbPageData = dbPage.getData();
        BitSetMask.clearBit(dbPageData, Global.DBPAGE_BITMAP_POS, index);
        dbPage.setDirty();
    }

    private static int nextClearBit(Page dbPage) {
        byte[] dbPageData = dbPage.getData();
        return BitSetMask.nextClearBit(dbPageData,Global.DBPAGE_BITMAP_POS,Global.DBPAGE_TABLE_POS);
    }
}
