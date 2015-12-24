package me.hqythu.object;

import me.hqythu.manager.SystemManager;
import me.hqythu.pagefile.*;
import me.hqythu.exception.SQLRecordException;
import me.hqythu.exception.SQLTableException;
import me.hqythu.util.SetValue;
import me.hqythu.util.Where;

/**
 * 表
 */
public class Table {

    String name;        // 表名
    int pageId;         // 表页的id
    int len;          // 记录的长度
    Column[] columns;   // 列属性

    public Table(String name, int index, int len, Column[] columns) {
        this.name = name;
        this.pageId = index;
        this.len = len;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }
    public int getPageId() {
        return pageId;
    }
    public Column[] getColumns() {
        return columns;
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

    //------------------------辅助函数------------------------
    // 记录的域名转为列位置
    protected int[] fieldsToCols(String[] fields) throws SQLTableException {
        int cols[] = new int[fields.length];
        for (int i = 0; i < cols.length; i++) {
            cols[i] = -1;
            for (int j = 0; j < columns.length; j++) {
                if (fields[i].equals(columns[j].name)) {
                    cols[i] = j;
                    break;
                }
            }
            if (cols[i] == -1) {
                throw new SQLTableException("not have column: " + fields[i]);
            }
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

            // 数据首页
            int dataPageId = TablePageUser.getFirstDataPage(tablePage);
            // 如果数据首页-1,表示无数据页
            if (dataPageId == -1) {
                dataPage = DbPageUser.getNewPage(dbPage);
                if (dataPage == null) throw new SQLTableException("can not getNewPage in INSERT");
                TablePageUser.setFirstDataPage(tablePage, dataPage.getPageId());
                DataPageUser.initPage(dataPage, record.length);
            } else {
                dataPage = BufPageManager.getInstance().getPage(fileId, dataPageId);
            }

            // 找到有空槽的页
            while(DataPageUser.isFull(dataPage)) {
                dataPageId = DataPageUser.getNextIndex(dataPage);
                if (dataPageId == -1) { // 无下一页,需要分配新页.肯定为空
                    dataPage2 = DbPageUser.getNewPage(dbPage);
                    if (dataPage2 == null) throw new SQLTableException("can not getNewPage in INSERT");
                    DataPageUser.initPage(dataPage2, record.length);
                    DataPageUser.connectPage(dataPage, dataPage2);
                    dataPage = dataPage2;
                } else {
                    dataPage = BufPageManager.getInstance().getPage(fileId,dataPageId);
                }
            }
            DataPageUser.addRecord(dataPage, record);
            TablePageUser.incRecordSize(tablePage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("insert failed");
        }
    }

    /**
     * 删除记录
     * 删除策略:
     * 遍历:每个数据页的每个记录
     * 用该页的最后一条记录取代被删除的记录
     * 每个数据页空则回收
     * 数据页之间不合并
     * （未完成，Where没有实现）
     */
    public void remove(Where where) throws SQLTableException {
        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = SystemManager.getInstance().getFileId();
        if (fileId == -1) throw new SQLTableException("have not open database");
        try {
            Page tablePage = BufPageManager.getInstance().getPage(fileId, pageId);

            // 每个数据页
            int firstPageId = TablePageUser.getFirstDataPage(tablePage);
            for (int dataPageId = firstPageId; dataPageId != -1;) {
                Page page = BufPageManager.getInstance().getPage(fileId,dataPageId);

                // 每条记录
                int size = DataPageUser.getRecordSize(page);
                for (int index = 0; index > size; ) {
                    byte[] data = DataPageUser.readRecord(page, index);
//                    byte[] data = TablePageUser.getRecord(tablePage, index);
                    if (where.match(data, columns)) {
                        DataPageUser.removeRecord(page, index);
                        size--;
                    } else {
                        index++;
                    }
                }

                dataPageId = DataPageUser.getNextIndex(page);
                if (size == 0 && page.getPageId() != firstPageId) { // 该页已清空，无记录
                    DataPageUser.removeConnectPage(page);            // 断开连接
                    DbPageUser.recyclePage(dbPage,page.getPageId()); // 回收该页
                } else {
                    DataPageUser.setRecordSize(page,size);
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
    public void update(Where where, SetValue setValue) throws SQLTableException {
        int fileId = SystemManager.getInstance().getFileId();
        if (fileId == -1) throw new SQLTableException("have not open database");

        try {
            Page tablePage = BufPageManager.getInstance().getPage(fileId, pageId);

            // 每个数据页
            int firstPageId = TablePageUser.getFirstDataPage(tablePage);
            for (int dataPageId = firstPageId; dataPageId != -1;) {
                Page page = BufPageManager.getInstance().getPage(fileId,dataPageId);

                // 每条记录
                int size = DataPageUser.getRecordSize(page);
                for (int index = 0; index > size; index++) {
                    byte[] data = DataPageUser.readRecord(page, index);
                    if (where.match(data, columns)) {
                        DataPageUser.removeRecord(page, index);
                        setValue.set(data, columns);
                        DataPageUser.writeRecord(page,index,data);
                    }
                }
                dataPageId = DataPageUser.getNextIndex(page);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("update failed");
        }
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
