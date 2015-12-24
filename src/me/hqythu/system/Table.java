package me.hqythu.system;

import me.hqythu.PageFile.*;
import me.hqythu.exception.SQLRecordException;
import me.hqythu.exception.SQLTableException;
import me.hqythu.record.Record;
import me.hqythu.util.Column;
import me.hqythu.util.Where;

/**
 * 表
 */
public class Table {

    String name;
    int pageId;
    short len;
    int nRecord;
    Column[] columns;

    public Table(String name, int index, short len, int nRecord, Column[] columns) {
        this.name = name;
        this.pageId = index;
        this.len = len;
        this.nRecord = nRecord;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    //------------------------预处理------------------------
    // 插入预处理
    // 将参数转为byte[]
    public void insert(Object[] values) throws SQLRecordException, SQLTableException {
        if (values == null) throw new SQLTableException("insert none value");
        if (values.length != columns.length) throw new SQLTableException("insert not enough columns");

        int[] cols = new int[values.length];
        for (int i = 0; i < cols.length; i++) {
            cols[i] = i;
        }

        insert(cols, values);
    }

    public void insert(String[] fields, Object[] values) throws SQLTableException, SQLRecordException {
        if (fields == null) throw new SQLTableException("insert none fields");

        // 对应列
        int[] cols = fieldsToCols(fields);

        insert(cols, values);
    }

    public void insert(int[] cols, Object[] values) throws SQLTableException, SQLRecordException {
        if (cols == null) throw new SQLTableException("insert none cols");
        if (values == null) throw new SQLTableException("insert none values");
        if (cols.length != values.length) throw new SQLTableException("insert none wrong data");

        byte[] record = Record.valueToByte(columns, len, cols, values);
        insert(record);
    }

    //------------------------辅助函数------------------------
    // 记录的域名转为列位置
    protected int[] fieldsToCols(String[] fields) throws SQLTableException {
        int cols[] = new int[fields.length];
        for (int i = 0; i < cols.length; i++) {
            cols[i] = -1;
            for (int j = 0; j < columns.length; j++) {
                if (fields.equals(columns[j].name)) {
                    cols[i] = j;
                    break;
                }
            }
            if (cols[i] == -1) throw new SQLTableException("not have column: " + fields[i]);
        }
        return cols;
    }

    //------------------------实际主函数------------------------
    /**
     * 删除所有记录
     */
    public void removeAll() throws SQLTableException {
        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = dbPage.getFileId();
        try {
            Page page = BufPageManager.getInstance().getPage(fileId, pageId);
            TablePageUser.removeAllRecord(page);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("remove all fialed");
        }
    }

    /**
     * 删除记录
     * （未完成，Where没有实现）
     */
    public void remove(Where where) throws SQLTableException {
        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = dbPage.getFileId();
        try {
            Page tablePage = BufPageManager.getInstance().getPage(fileId, pageId);
            int firstPageId = TablePageUser.getDataPageIndex(tablePage);
            int dataPageId = firstPageId;
            if (dataPageId == -1) return;
            where.setFromCols(columns);
            while (dataPageId != -1) {
                Page page = BufPageManager.getInstance().getPage(fileId,dataPageId);
                int size = DataPageUser.getRecordSize(page);
                int index = 0;
                while (index < size) {
                    byte[] data = DataPageUser.readRecord(page, index);
//                    byte[] data = TablePageUser.getRecord(tablePage, index);
                    if (where.match(data)) {
                        DataPageUser.removeRecord(tablePage, index);
                        size--;
                    } else {
                        index++;
                    }
                }
                dataPageId = DataPageUser.getNextPageId(page);
                if (size == 0) { // 该页已清空，无记录
                    if (dataPageId == firstPageId) {
                        TablePageUser.setDataPageIndex(tablePage, dataPageId);
                    }
                    DataPageUser.removeConnectPage(page);            // 断开连接
                    DbPageUser.recyclePage(dbPage,page.getPageId()); // 回收该页
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("remove failed");
        }
    }


    public void update(String[] fields, Object[] values, Where where) throws SQLTableException {
        if (fields == null) throw new SQLTableException("updata none cols");
        int[] cols = fieldsToCols(fields);
        update(cols,values,where);
    }

    // 这里未完成
    public void update(int[] cols, Object[] values, Where where) {

    }

//    public QuerySet query(String[] fields, SelectOption option, Where where) {
//        return null;
//    }
//
//    public QuerySet query(int[] cols, SelectOption option, Where where) {
//        return null;
//    }

    /**
     * 插入记录，处理数据页
     */
    protected void insert(byte[] record) throws SQLTableException {

        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = dbPage.getFileId();
        Page tablePage;
        Page dataPage;
        Page dataPage2;

        try {
            tablePage = BufPageManager.getInstance().getPage(fileId, pageId);
            int dataPageId = TablePageUser.getDataPageIndex(tablePage);
            if (dataPageId == -1) {
                dataPage = DbPageUser.getNewPage(dbPage);
                DataPageUser.initDataPage(dataPage, (short) record.length);
            } else {
                dataPage = BufPageManager.getInstance().getPage(fileId, pageId);
            }

            // 已经满了，则增加新页
            // 插入数据
            if (DataPageUser.isFull(dataPage)) {
                dataPage2 = DbPageUser.getNewPage(dbPage);
                DataPageUser.initDataPage(dataPage, (short) record.length);
                DataPageUser.connectPage(dataPage, dataPage2);
                DataPageUser.writeRecord(dataPage2, record);
            } else {
                DataPageUser.writeRecord(dataPage, record);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("insert failed");
        }
    }

}