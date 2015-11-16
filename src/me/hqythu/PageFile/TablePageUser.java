package me.hqythu.PageFile;

import me.hqythu.system.SystemManager;
import me.hqythu.util.Global;
import me.hqythu.record.Record;
import me.hqythu.system.Table;
import me.hqythu.util.Column;
import me.hqythu.util.DataType;

import java.nio.ByteBuffer;

public class TablePageUser {

    private TablePageUser() {
    }

    /**
     * 初始化表首页
     */
    public static void initTablePage(Page page, String tableName, Column[] columns) {
        ByteBuffer buffer = page.getBuffer();
        buffer.position(0);
        buffer.put(tableName.getBytes());  // 表名 112
        buffer.putInt(Global.TBPAGE_PAGEIDX_POS, page.getPageId());        // 表页索引 4
        buffer.putInt(Global.TBPAGE_DATAIDX_POS, -1);                      // 数据页首页索引 4
        buffer.putShort(Global.TBPAGE_COLNUM_POS, (short) columns.length); // 列数 2
        buffer.putShort(Global.TBPAGE_RECORDLEN_POS, Record.getRecordLen(columns)); // 记录长度 2
        buffer.putInt(Global.TBPAGE_RECORDNUM_POS, 0);                     // 记录总数 4

        for (int i = 0; i < columns.length; i++) {
            int pos = Global.PER_COL_INFO_POS + i * Global.PER_COL_INFO_LEN;
            buffer.position(pos);
            buffer.put(columns[i].name.getBytes());                              // 列名称 120, 未检查
            buffer.putInt(pos + Global.COL_PROP_POS, columns[i].prop);           // 数据属性 4
            buffer.putShort(pos + Global.COL_TYPE_POS, (short) columns[i].type.ordinal()); // 数据类型 2
            buffer.putShort(pos + Global.COL_LEN_POS, columns[i].len);             // 数据长度 2
        }
        page.setDirty();
    }

    public static void setDataPageIndex(Page page, int dataPageId) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.TBPAGE_DATAIDX_POS, dataPageId);
    }

    /**
     * 读取表首页，得到Table
     */
    public static Table readTablePage(Page page) {
        byte[] data = page.getData();
        ByteBuffer buffer = page.getBuffer();

        // 表信息
        String tableName = new String(data, Global.TBPAGE_NAME_POS, Global.TABLE_NAME_LEN);  // 表名
        int index = buffer.getInt(Global.TBPAGE_PAGEIDX_POS);                // 表页索引 4
        short recordLen = buffer.getShort(Global.TBPAGE_RECORDLEN_POS);      // 每条记录长度
        int nRecord = buffer.getInt(Global.TBPAGE_RECORDNUM_POS);            // 记录总数
        int n = buffer.getShort(Global.TBPAGE_COLNUM_POS);                   // 列数

        // 列信息
        Column[] cols = new Column[n];
        for (int i = 0; i < n; i++) {
            int offset = Global.PER_COL_INFO_POS + i * Global.PER_COL_INFO_LEN;

            String name = new String(data, offset + Global.COL_NAME_POS, Global.COL_NAME_LEN); // 列名
            int prop = buffer.getInt(offset + Global.COL_PROP_POS);                            // 列属性
            DataType type = DataType.valueOf(buffer.getShort(offset + Global.COL_TYPE_POS)); // 数据类型
            short len = buffer.getShort(offset + Global.COL_LEN_POS);                          // 数据列长

            cols[i] = new Column(name, prop, type, len);
        }
        return new Table(tableName, index, recordLen, nRecord, cols);
    }

    public static int getDataPageIndex(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.TBPAGE_PAGEIDX_POS);
    }

    public static Page getRecordPage(Page page, int recordId) {
        Page dbPage = SystemManager.getInstance().getDbPage();
        ByteBuffer buffer = page.getBuffer();
        int fileId = dbPage.getFileId();
        int dataPageId = buffer.getInt(Global.TBPAGE_DATAIDX_POS);
        int total = 0;
        try {
            while (dataPageId != -1) {
                Page dataPage = BufPageManager.getInstance().getPage(fileId, dataPageId);
                total += DataPageUser.getRecordSize(dataPage);
                if (total >= recordId) return dataPage;
                else dataPageId = DataPageUser.getNextPageId(dataPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getRecord(Page page, int recordId) {
        Page dbPage = SystemManager.getInstance().getDbPage();
        ByteBuffer buffer = page.getBuffer();
        int fileId = dbPage.getFileId();
        int dataPageId = buffer.getInt(Global.TBPAGE_DATAIDX_POS);
        try {
            Page dataPage = null;
            while (dataPageId != -1) {
                dataPage = BufPageManager.getInstance().getPage(fileId,dataPageId);
                int size = DataPageUser.getRecordSize(dataPage);
                if (size >= recordId) {
                    break;
                } else {
                    recordId -= size;
                    dataPageId = DataPageUser.getNextPageId(dataPage);
                }
            }
            if (dataPageId == -1) {
                return null;
            } else {
                return DataPageUser.getRecord(dataPage, recordId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void removeAllRecord(Page page) {
        Page dbPage = SystemManager.getInstance().getDbPage();
        ByteBuffer buffer = page.getBuffer();
        int fileId = dbPage.getFileId();
        int dataPageId = buffer.getInt(Global.TBPAGE_DATAIDX_POS);
        try {
            while (dataPageId != -1) {
                Page dataPage = BufPageManager.getInstance().getPage(fileId, dataPageId);
                DbPageUser.recyclePage(dbPage, dataPageId);
                dataPageId = DataPageUser.getNextPageId(dataPage);
            }
            buffer.putInt(Global.TBPAGE_DATAIDX_POS, -1);
            buffer.putInt(Global.TBPAGE_RECORDNUM_POS, 0);
            page.setDirty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getRecordSize(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.TBPAGE_RECORDNUM_POS);
    }
}
