package me.hqythu.pagefile;

import me.hqythu.util.Global;
import me.hqythu.object.Table;
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
        setTableSize(page,0);
        // 初始化页bitmap
        initBitMap(page);
        // 第一页标记1，已被使用
        setBitMap(page, 0);

        page.setDirty();
    }

    public static void initTableFromPage(Page dbPage, Map<String, Table> tables) {
        int fileId = dbPage.getFileId();
        int nTable = getTableSize(dbPage);

        // 初始化表信息
        try {
            for (int i = 0; i < nTable; i++) {
                String name = getTableName(dbPage,i);
                int pageIndex = getTableIdx(dbPage,i);
                Page page = BufPageManager.getInstance().getPage(fileId, pageIndex);
                Table table = TablePageUser.getTable(page);
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
        int nTable = getTableSize(dbPage);
        setTableName(dbPage,nTable,tableName);     // 表名
        setTableIdx(dbPage,nTable,tablePageIndex); // 表的位置
        incTableSize(dbPage);
        dbPage.setDirty();
    }

    /**
     * 从数据库首页删除表的信息
     */
    public static int delTableInfo(Page dbPage, String tableName) {
        byte[] data = dbPage.getData();
        int pageIndex = -1;
        int size = getTableSize(dbPage);
        int i; // 找到被删除的表信息的位置
        for (i = 0; i < size; i++) {
            String name = getTableName(dbPage,i);
            if (name.equals(tableName)) { // 删除表信息
                pageIndex = getTableIdx(dbPage,i);
                break;
            }
        }
        // 最后一条表信息移到被删除的位置
        if (pageIndex != -1) {
            if (i < size - 1) {
                System.arraycopy(data, Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN,
                        data, Global.DBPAGE_TABLE_POS + (size-1) * Global.PER_TABLE_INFO_LEN,
                        Global.PER_TABLE_INFO_LEN
                        );
            }
            decTableSize(dbPage);
            dbPage.setDirty();
        }
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


    //------------------------获取页信息------------------------
    public static void setTableSize(Page dbPage, int size) {
        ByteBuffer buffer = dbPage.getBuffer();
        buffer.putInt(Global.DBPATE_INFO_TABLESIZE, size);
    }
    public static int getTableSize(Page dbPage) {
        ByteBuffer buffer = dbPage.getBuffer();
        return buffer.getInt(Global.DBPATE_INFO_TABLESIZE);
    }
    public static void incTableSize(Page dbPage) {
        int temp = getTableSize(dbPage);
        temp++;
        setTableSize(dbPage,temp);
    }
    public static void decTableSize(Page dbPage) {
        int temp = getTableSize(dbPage);
        temp--;
        setTableSize(dbPage,temp);
    }
    public static int getTableOffset(int index) {
        return Global.DBPAGE_TABLE_POS + index * Global.PER_TABLE_INFO_LEN;
    }
    public static int getTableIdx(Page dbPage, int i) {
        ByteBuffer buffer = dbPage.getBuffer();
        return buffer.getInt(Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN);
    }
    public static void setTableIdx(Page dbPage, int i, int index) {
        ByteBuffer buffer = dbPage.getBuffer();
        buffer.putInt(Global.DBPAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN,index);
    }
    public static String getTableName(Page dbPage, int index) {
        byte[] data = dbPage.getData();
        String temp = new String(data,getTableOffset(index)+4,Global.TABLE_NAME_LEN);
        if (temp.indexOf(0) > 0) {
            return temp.substring(0,temp.indexOf(0));
        } else {
            return temp;
        }
    }
    public static void setTableName(Page dbPage, int index, String name) {
        ByteBuffer buffer = dbPage.getBuffer();
        byte[] data = name.getBytes();
        int offset = getTableOffset(index);
        buffer.position(offset + 4);
        if (data.length > Global.TABLE_NAME_LEN) {
            buffer.put(data,0,Global.TABLE_NAME_LEN);
        } else {
            buffer.put(name.getBytes());
        }
    }



    private static void initBitMap(Page page) {
        ByteBuffer buffer = page.getBuffer();
        Arrays.fill(buffer.array(), Global.DBPAGE_BITMAP_POS, Global.DBPAGE_TABLE_POS, (byte) 0);
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
