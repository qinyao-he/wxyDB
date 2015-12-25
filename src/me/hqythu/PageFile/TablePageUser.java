package me.hqythu.pagefile;

import me.hqythu.manager.SystemManager;
import me.hqythu.util.Global;
import me.hqythu.object.Record;
import me.hqythu.object.Table;
import me.hqythu.object.Column;
import me.hqythu.object.DataType;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TablePageUser {

    public static final int TABLE_PROP_ALLOW_NULL = 0x80000000;
    public static final int TABLE_PROP_HAS_PRIMARY = 0x40000000;

    private TablePageUser() {
    }

    /**
     * 初始化表首页
     */
    public static void initPage(Page page, String tableName, Column[] columns) {

        setName(page,tableName);                // 表名 108
        setFirstDataPage(page,-1);              // 首数据页索引 4
        setTableIndex(page,page.getPageId());   // 表索引 4
        setRecordLen(page,Record.getRecordLen(columns)); // 记录长度 4
        setRecordSize(page,0); // 记录个数
        setColumnSize(page,(short) columns.length); // 列数 2
        setPrimaryCol(page,-1);
        for (int i = 0; i < columns.length; i++) {
            setColumn(page,i,columns[i]);
            if (columns[i].isPrimary()) {
                setPrimaryCol(page,i);
                break;
            }
        }
        page.setDirty();
    }

    /**
     * 读取表首页，得到Table
     */
    public static Table getTable(Page page) {

        // 表信息
        String tableName = getName(page); // 表名
        int index = getTableIndex(page); // 表页索引
        int recordLen = getRecordLen(page); // 每条记录长度
        int n = getColumnSize(page); // 列数

        // 列信息
        Column[] cols = new Column[n];
        for (int i = 0; i < n; i++) {
            cols[i] = getColumn(page,i);
        }
        return new Table(tableName, index, recordLen, cols);
    }

    /**
     * 按id获取某个记录所在的页
     * @param page
     * @param recordId
     * @return
     */
    public static Page getRecordPage(Page page, int recordId) {
        int fileId = SystemManager.getInstance().getFileId();
        int dataPageId = getFirstDataPage(page);
        int total = 0;
        try {
            while (dataPageId != -1) {
                Page dataPage = BufPageManager.getInstance().getPage(fileId, dataPageId);
                total += DataPageUser.getRecordSize(dataPage);
                if (total >= recordId) return dataPage;
                else dataPageId = DataPageUser.getNextIndex(dataPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按id获取某个记录
     * @param page
     * @param recordId
     * @return
     */
    public static byte[] getRecord(Page page, int recordId) {
        int fileId = SystemManager.getInstance().getFileId();
        int dataPageId = getFirstDataPage(page);
        try {
            Page dataPage = null;
            while (dataPageId != -1) {
                dataPage = BufPageManager.getInstance().getPage(fileId,dataPageId);
                int size = DataPageUser.getRecordSize(dataPage);
                if (size >= recordId) {
                    break;
                } else {
                    recordId -= size;
                    dataPageId = DataPageUser.getNextIndex(dataPage);
                }
            }
            if (dataPageId == -1) {
                return null;
            } else {
                return DataPageUser.readRecord(dataPage, recordId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<byte[]> getAllRecords(Page page) {

        int fileId = SystemManager.getInstance().getFileId();
        if (fileId == -1) return null;

        try {
            int totalSize = TablePageUser.getRecordSize(page);
            List<byte[]> datas = new ArrayList<>(totalSize);

            // 每个数据页
            int firstPageId = TablePageUser.getFirstDataPage(page);
            for (int dataPageId = firstPageId; dataPageId != -1;) {
                Page dPage = BufPageManager.getInstance().getPage(fileId,dataPageId);

                // 每条记录
                int size = DataPageUser.getRecordSize(dPage);
                for (int index = 0; index < size; index++) {
                    byte[] data = DataPageUser.readRecord(dPage, index);
                    datas.add(data);
                }
                dataPageId = DataPageUser.getNextIndex(dPage);
            }
            return datas;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除所有记录
     * @param page
     */
    public static void removeAllRecord(Page page) {
        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = SystemManager.getInstance().getFileId();
        int dataPageId = getFirstDataPage(page);
        try {
            while (dataPageId != -1) {
                Page dataPage = BufPageManager.getInstance().getPage(fileId, dataPageId);
                DbPageUser.recyclePage(dbPage, dataPageId);
                dataPageId = DataPageUser.getNextIndex(dataPage);
            }
            setFirstDataPage(page,-1);
            setRecordSize(page,0);
            page.setDirty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //------------------------获取页信息------------------------
    public static String getName(Page page) {
        byte[] data = page.getData();
        String temp = new String(data,Global.TBPAGE_NAME_POS,Global.TABLE_NAME_LEN);
        return temp.substring(0,temp.indexOf(0));
    }
    public static void setName(Page page, String name) {
        ByteBuffer buffer = page.getBuffer();
        buffer.position(Global.TBPAGE_NAME_POS);
        byte[] data = name.getBytes();
        if (data.length > Global.TABLE_NAME_LEN) {
            buffer.put(data,0,Global.TABLE_NAME_LEN);
        } else {
            buffer.put(data);
        }
    }
    public static int getFirstDataPage(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.TBPAGE_DATAIDX_POS);
    }
    public static void setFirstDataPage(Page page, int dataPageId) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.TBPAGE_DATAIDX_POS, dataPageId);
    }
    public static int getRecordSize(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.TBPAGE_RECORD_SIZE_POS);
    }
    public static void setRecordSize(Page page, int size) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.TBPAGE_RECORD_SIZE_POS, size);
    }
    public static void incRecordSize(Page page) {
        int size = getRecordSize(page);
        setRecordSize(page,++size);
    }
    public static void decRecordSize(Page page) {
        int size = getRecordSize(page);
        setRecordSize(page,--size);
    }
    public static int getTableIndex(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.TBPAGE_PAGEIDX_POS);
    }
    public static void setTableIndex(Page page, int index) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.TBPAGE_PAGEIDX_POS, page.getPageId());
    }
    public static void setColumnSize(Page page, short size) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putShort(Global.TBPAGE_COLUMN_POS, size);
    }
    public static short getColumnSize(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getShort(Global.TBPAGE_COLUMN_POS);
    }
    public static void setRecordLen(Page page, int len) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.TBPAGE_RECORD_LEN_POS, len);
    }
    public static int getRecordLen(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.TBPAGE_RECORD_LEN_POS);
    }
    public static void setAllowNull(Page page) {
        ByteBuffer buffer = page.getBuffer();
        int flag = buffer.getInt(Global.TBPAGE_PROP_POS);
        flag |= TABLE_PROP_ALLOW_NULL;
        buffer.putInt(Global.TBPAGE_PROP_POS,flag);
    }
    public static void clearAllowNull(Page page) {
        ByteBuffer buffer = page.getBuffer();
        int flag = buffer.getInt(Global.TBPAGE_PROP_POS);
        flag &= ~TABLE_PROP_ALLOW_NULL;
        buffer.putInt(Global.TBPAGE_PROP_POS,flag);
    }
    public static void setPrimaryCol(Page page, int col) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.TBPAGE_PRIMARY_POS,col);
    }
    public static int getPrimaryCol(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.TBPAGE_PRIMARY_POS);
    }
    public static Column getColumn(Page page, int index) {
        ByteBuffer buffer = page.getBuffer();
        byte[] data = page.getData();
        int offset = Global.COL_INFO_POS + index * Global.PER_COL_INFO_LEN;

        String name = new String(data, offset + Global.COL_NAME_POS, Global.COL_NAME_LEN); // 列名
        name = name.substring(0,name.indexOf(0)); // 去掉尾部的0
        int prop = buffer.getInt(offset + Global.COL_PROP_POS);                            // 列属性
        DataType type = DataType.valueOf(buffer.getShort(offset + Global.COL_TYPE_POS)); // 数据类型
        short len = buffer.getShort(offset + Global.COL_LEN_POS);                          // 数据列长

        return new Column(name, type, len, prop);
    }
    public static void setColumn(Page page, int index, Column col) {
        ByteBuffer buffer = page.getBuffer();
        int pos = Global.COL_INFO_POS + index * Global.PER_COL_INFO_LEN;

        buffer.position(pos);
        byte[] data = col.name.getBytes();
        if (data.length > Global.COL_NAME_LEN) { // 列名称
            buffer.put(data,0,Global.COL_NAME_LEN);
        } else {
            buffer.put(data);
        }
        buffer.putInt(pos + Global.COL_PROP_POS, col.prop);           // 数据属性 4
        buffer.putShort(pos + Global.COL_TYPE_POS, (short) col.type.ordinal()); // 数据类型 2
        buffer.putShort(pos + Global.COL_LEN_POS, col.len);             // 数据长度 2
    }
}
