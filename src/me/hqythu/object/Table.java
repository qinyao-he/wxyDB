package me.hqythu.object;

import me.hqythu.manager.SystemManager;
import me.hqythu.pagefile.*;
import me.hqythu.exception.SQLRecordException;
import me.hqythu.exception.SQLTableException;
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

    public void update(String[] fields, Object[] values, Where where) throws SQLTableException {
        if (fields == null) throw new SQLTableException("updata none cols");
        int[] cols = fieldsToCols(fields);
        update(cols,values,where);
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
            int dataPageId = TablePageUser.getFirstDataPage(tablePage);
            if (dataPageId == -1) {
                dataPage = DbPageUser.getNewPage(dbPage);
                DataPageUser.initPage(dataPage, (short) record.length);
            } else {
                dataPage = BufPageManager.getInstance().getPage(fileId, pageId);
            }

            // 已经满了，则增加新页
            // 插入数据
            if (DataPageUser.isFull(dataPage)) {
                dataPage2 = DbPageUser.getNewPage(dbPage);
                DataPageUser.initPage(dataPage, (short) record.length);
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

    /**
     * 删除记录
     * 删除策略:用该页的最后一条记录取代
     * （未完成，Where没有实现）
     */
    public void remove(Where where) throws SQLTableException {
        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = dbPage.getFileId();
        try {
            Page tablePage = BufPageManager.getInstance().getPage(fileId, pageId);
            int firstPageId = TablePageUser.getFirstDataPage(tablePage);
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
                dataPageId = DataPageUser.getNextIndex(page);
                if (size == 0) { // 该页已清空，无记录
                    if (dataPageId == firstPageId) {
                        TablePageUser.setFirstDataPage(tablePage, dataPageId);
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

    /**
     * 更新记录
     * 先取出记录,将对应列的数据改写,再写回
     * （未完成）
     */
    // 这里未完成
    public void update(int[] cols, Object[] values, Where where) {

    }

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
}
