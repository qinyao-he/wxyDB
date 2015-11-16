package me.hqythu.system;

import me.hqythu.Global;
import me.hqythu.record.BufPageManager;
import me.hqythu.record.FilePageManager;
import me.hqythu.record.Page;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class SystemManager {

    // connectDB是否为null，决定其他是否为null
    String connectDB = null;

    Map<String, Table> tables = null;
    int fileId;

    private static final byte[] aSetMask = {
            (byte) 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01
    };
    private static final byte[] aClearMask = {
            (byte) 0x7f, (byte) 0xbf, (byte) 0xdf, (byte) 0xef,
            (byte) 0xf7, (byte) 0xfb, (byte) 0xfd, (byte) 0xfe
    };

    private static SystemManager manager = null;
    private SystemManager(){}
    public static SystemManager getInstance() {
        if (manager == null) {
            manager = new SystemManager();
        }
        return manager;
    }


    /**
     * 创建DB
     */
    public boolean createDatabase(String DBname) {
        try {
            File file = new File(DBname);
            if (!file.createNewFile()) return false;
            int tempFileId = FilePageManager.getInstance().openFile(DBname);
            Page tempPage = BufPageManager.getInstance().getPage(tempFileId, 0);

            // 数据库文件第一页初始化
            ByteBuffer buffer = tempPage.getBuffer();
            buffer.putInt(Global.FIRST_PAGE_INFO_POS + 0, 0); // 初始化表的个数
            buffer.position(Global.FIRST_PAGE_BITMAP_POS);    // 初始化页bitmap
            for (int i = 0; i < Global.FIRST_PAGE_BITMAP_LEN; i++) {
                buffer.put((byte) 0);
            }
            buffer.put(Global.FIRST_PAGE_BITMAP_POS, (byte) 0x80); // 第一页标记1，已被使用
            tempPage.setDirty();
            BufPageManager.getInstance().releasePage(tempPage);
            FilePageManager.getInstance().closeFile(tempFileId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除DB
     */
    public boolean dropDatabase(String DBname) {
        if (connectDB != null && connectDB.equals(DBname)) {
            closeDatabase();
        }
        File file = new File(DBname);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 选择DB
     */
    public boolean useDatabase(String DBname) {
        if (DBname == null) return false;
        if (connectDB != null && connectDB.equals(DBname)) {
            return true;
        }
        closeDatabase();
        return openDatabase(DBname);


    }

    protected void closeDatabase() {
        if (connectDB != null) {
            connectDB = null;
            tables.clear();

            BufPageManager.getInstance().clear();
            FilePageManager.getInstance().closeFile(fileId);
        }
    }

    protected boolean openDatabase(String DBname) {
        connectDB = DBname;
        fileId = FilePageManager.getInstance().openFile(DBname);
        if (fileId == -1) return false;

        // 切换DB的初始化
        try {
            Page firstPage = BufPageManager.getInstance().getPage(fileId, 0);

            byte[] firstPageData = firstPage.getData();
            ByteBuffer firstPageBuffer = firstPage.getBuffer();

            int tableSize = firstPageBuffer.getInt(Global.FIRST_PAGE_INFO_POS);

            // 初始化表信息
            tables = new HashMap<>();
            try {
                for (int i = 0; i < tableSize; i++) {
                    int offset = Global.FIRST_PAGE_TABLE_POS + i * Global.PER_TABLE_INFO_LEN;
                    int tablePageId = firstPageBuffer.getInt(offset);
                    offset += 4;
                    String name = new String(firstPageData, offset, Global.PER_TABLE_INFO_LEN - 4, "utf8");
                    Page page = BufPageManager.getInstance().getPage(fileId, tablePageId);
                    Table table = new Table(page);
                    tables.put(name, table);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            firstPage.setDirty();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 创建表
     */
    public boolean createTable(String tableName, String[] names, Table.DataType[] types) {
        if (connectDB == null) return false;
        try {
            int pos = Global.FIRST_PAGE_TABLE_POS + tables.size() * Global.PER_TABLE_INFO_LEN;
            int tablePageId = nextClearBit();

            Page firstPage = BufPageManager.getInstance().getPage(fileId, 0);
            byte[] firstPageData = firstPage.getData();
            ByteBuffer firstPageBuffer = firstPage.getBuffer();

            firstPageBuffer.putInt(pos, tablePageId); // 表的位置
            firstPageBuffer.position(pos + 4);
            firstPageBuffer.put(tableName.getBytes("utf8"));
            firstPageBuffer.put((byte) 0);
            firstPage.setDirty();

            Page tablePage = BufPageManager.getInstance().getPage(fileId, tablePageId);
            ByteBuffer buffer = tablePage.getBuffer();
            buffer.position(0);
            buffer.put(firstPageData, pos, Global.PER_TABLE_INFO_LEN);
            for (int i = 0; i < names.length; i++) {
                pos = Global.PER_COL_INFO_POS + i * Global.PER_COL_INFO_LEN;
                buffer.position(pos);
                buffer.putLong(types[i].ordinal());
                buffer.put(names[i].getBytes("utf8"));
                buffer.put((byte) 0);
            }

            tablePage.setDirty();
            tablePage.writeBack();
            tables.put(tableName, new Table(tablePage));
            firstPageBuffer.putInt(Global.FIRST_PAGE_INFO_POS,tables.size());
            setBitMap(tablePageId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 删除表（未完成）
     */
    public boolean dropTable(String tableName) {
        if (connectDB == null) return false;
        return false;
    }

    /**
     * 显示表（未完成）
     */
    public Object[] showTable() {
        if (connectDB == null) return null;
        return tables.keySet().toArray();
    }

    public Table getTable(String tableName) {

        return tables.get(tableName);
    }

    /**
     * 页bitmap
     * 分配空闲页
     */
    public int nextClearBit() {
        if (connectDB == null) throw new RuntimeException("have not use db");
        try {
            Page firstPage = BufPageManager.getInstance().getPage(fileId,0);
            byte[] firstPageData = firstPage.getData();
            for (int i = Global.FIRST_PAGE_BITMAP_POS; i < Global.FIRST_PAGE_TABLE_POS; i++) {
                byte b = firstPageData[i];
                for (int j = 0; j < 8; j++) {
                    if ((b & aSetMask[j]) == 0) return (i - Global.FIRST_PAGE_BITMAP_POS) * 8 + j;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setBitMap(int index) {
        if (connectDB == null) throw new RuntimeException("have not use db");
        if (index < 0) throw new RuntimeException("illegal page bitmap index");
        int pos = Global.FIRST_PAGE_BITMAP_POS + (int) (index / 8);
        try {

            Page firstPage = BufPageManager.getInstance().getPage(fileId,0);
            byte[] firstPageData = firstPage.getData();
            firstPageData[pos] |= aSetMask[index % 8];
            firstPage.setDirty();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearBitMap(int index) {
        if (connectDB == null) throw new RuntimeException("have not use db");
        if (index < 0) throw new RuntimeException("illegal page bitmap index");
        int pos = Global.FIRST_PAGE_BITMAP_POS + (int) (index / 8);
        try {
            Page firstPage = BufPageManager.getInstance().getPage(fileId,0);
            byte[] firstPageData = firstPage.getData();
            firstPageData[pos] &= aClearMask[index % 8];
            firstPage.setDirty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

    }
}
