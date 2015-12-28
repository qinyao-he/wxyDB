package me.hqythu.wxydb.object;

import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.pagefile.*;
import me.hqythu.wxydb.exception.SQLRecordException;
import me.hqythu.wxydb.exception.SQLTableException;
import me.hqythu.wxydb.pagefile.*;
import me.hqythu.wxydb.util.SetValue;
import me.hqythu.wxydb.util.Where;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表
 */
public class Table {

    private String name;        // 表名
    private int pageId;         // 表页的id
    private int recordLen;          // 记录的长度
    private Column[] columns;   // 列属性
    private int[] offsets;      // 列偏移


    public Table(String name, int index, int recordLen, Column[] columns) {
        this.name = name;
        this.pageId = index;
        this.recordLen = recordLen;
        this.columns = columns;
        offsets = new int[columns.length + 1];
        offsets[0] = 0;
        for (int i = 0; i < columns.length; i++) {
            offsets[i + 1] = offsets[i] + columns[i].len;
        }
    }

    public String getName() {
        return name;
    }

    public int getRecordLen() {
        return recordLen;
    }

    public int getPageId() {
        return pageId;
    }

    public Column[] getColumns() {
        return columns;
    }

    public int[] getOffsets() {
        return offsets;
    }
    //------------------------预处理------------------------
    // 插入预处理
    // 将参数转为byte[]

    // values个数不足,需要补null
    public void insert(String[] fields, Object[] values) throws SQLTableException {

        if (fields == null) throw new SQLTableException("insert none fields");

        Object[] newValues = new Object[columns.length];
        for (int i = 0; i < newValues.length; i++) {
            newValues[i] = null;
        }
        for (int i = 0; i < fields.length; i++) {
            int col = fieldToCol(fields[i]);
            newValues[col] = values[i];
        }
        insert(newValues);
    }

    // values的个数
    public void insert(Object[] values) throws SQLTableException {
        System.out.println("values:"+values.length+","+"columns:"+columns.length);
        if (values.length != columns.length) throw new SQLTableException("insert columns size not enough");
        try {
            byte[] record = Record.valuesToBytes(this, values);
            insert(record);
        } catch (Exception e) {
//            e.printStackTrace();
            throw new SQLTableException(e.getMessage());
        }
    }

    //------------------------实际主函数------------------------

    /**
     * 插入记录，处理数据页
     */
    public void insert(byte[] record) throws SQLTableException {

        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = dbPage.getFileId();
        Page tablePage;
        Page dataPage;
        Page dataPage2;

        try {
            tablePage = BufPageManager.getInstance().getPage(fileId, pageId);

            if (!checkPrimaryOk(record)) {
                throw new SQLTableException("can not insert duplicate primary key");
            }

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
            while (DataPageUser.isFull(dataPage)) {
                dataPageId = DataPageUser.getNextIndex(dataPage);
                if (dataPageId == -1) { // 无下一页,需要分配新页.肯定为空
                    dataPage2 = DbPageUser.getNewPage(dbPage);
                    if (dataPage2 == null) throw new SQLTableException("can not getNewPage in INSERT");
                    DataPageUser.initPage(dataPage2, record.length);
                    DataPageUser.connectPage(dataPage, dataPage2);
                    dataPage = dataPage2;
                } else {
                    dataPage = BufPageManager.getInstance().getPage(fileId, dataPageId);
                }
            }
            DataPageUser.addRecord(dataPage, record);
            TablePageUser.incRecordSize(tablePage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("insert failed: " + e.getMessage());
        }
    }

    /**
     * 删除记录
     * <p>
     * 删除策略:
     * 遍历每个数据页的每个记录
     * 用该页的最后一条记录取代被删除的记录
     * 每个数据页空则回收,数据页之间不合并
     */
    public void remove(Where where) throws SQLTableException {
        Page dbPage = SystemManager.getInstance().getDbPage();
        int fileId = SystemManager.getInstance().getFileId();
        if (fileId == -1) throw new SQLTableException("have not open database");
        try {
            Page tablePage = BufPageManager.getInstance().getPage(fileId, pageId);
            int total = TablePageUser.getRecordSize(tablePage);

            // 每个数据页
            int firstPageId = TablePageUser.getFirstDataPage(tablePage);
            for (int dataPageId = firstPageId; dataPageId != -1; ) {
                Page page = BufPageManager.getInstance().getPage(fileId, dataPageId);

                // 每条记录
                int size = DataPageUser.getRecordSize(page);
                for (int index = 0; index < size; ) {
                    byte[] data = DataPageUser.readRecord(page, index);
                    Object[] values = Record.bytesToValues(this, data);
                    Map<Table, Object[]> records = new HashMap<>();
                    records.put(this, values);
                    if (where.match(records, SystemManager.getInstance().getTables())) {
                        DataPageUser.removeRecord(page, index);
                        size--;
                        total--;
                    } else {
                        index++;
                    }
                }
                dataPageId = DataPageUser.getNextIndex(page);
                if (size == 0 && page.getPageId() != firstPageId) { // 该页已清空，无记录
                    DataPageUser.removeConnectPage(page);            // 断开连接
                    DbPageUser.recyclePage(dbPage, page.getPageId()); // 回收该页
                } else {
                    DataPageUser.setRecordSize(page, size);
                    page.setDirty();
                }
            }

            TablePageUser.setRecordSize(tablePage, total);
            tablePage.setDirty();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("remove failed");
        }
    }

    /**
     * 更新记录
     * 先取出记录,将对应列的数据改写,再写回
     * <p>
     * 未完成
     * 未考虑primary key
     */
    public void update(Where where, List<SetValue> setValues) throws SQLTableException {
        int fileId = SystemManager.getInstance().getFileId();
        if (fileId == -1) throw new SQLTableException("have not open database");

        try {
            Page tablePage = BufPageManager.getInstance().getPage(fileId, pageId);

            // 每个数据页
            int firstPageId = TablePageUser.getFirstDataPage(tablePage);
            for (int dataPageId = firstPageId; dataPageId != -1; ) {
                Page page = BufPageManager.getInstance().getPage(fileId, dataPageId);

                // 每条记录
                int size = DataPageUser.getRecordSize(page);
                for (int index = 0; index < size; index++) {
                    byte[] data = DataPageUser.readRecord(page, index);
                    Object[] values = Record.bytesToValues(this, data);
                    Map<Table, Object[]> records = new HashMap<>();
                    records.put(this, values);
                    // 满足条件的进行更新
                    if (where.match(records, SystemManager.getInstance().getTables())) {
                        for (SetValue setValue : setValues) {
                            int col = getColumnCol(setValue.columnName);
                            values[col] = setValue.calcValue(values[col]);
                        }
                        data = Record.valuesToBytes(this, values);
                        DataPageUser.writeRecord(page, index, data);
                        page.setDirty();
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
     * 获取所有记录
     *
     * @return
     * @throws SQLTableException
     */
    public List<Object[]> getAllRecords() throws SQLTableException {
        int fileId = SystemManager.getInstance().getFileId();
        if (fileId == -1) throw new SQLTableException("can not get DB fileId");
        List<byte[]> datas;
        try {
            Page page = BufPageManager.getInstance().getPage(fileId, pageId);
            datas = TablePageUser.getAllRecords(page);
        } catch (Exception e) {
            datas = null;
        }
        if (datas == null) throw new SQLTableException("can not get DB fileId");
        List<Object[]> records = new ArrayList<>(datas.size());
        records.addAll(datas.stream().map(data -> Record.bytesToValues(this, data)).collect(Collectors.toList()));

        return records;
    }

    /**
     * 获得所有记录的id
     */
    public List<Integer> getAllRecordIds() throws SQLTableException {
        int size = getRecordSize();
        if (size < 0) {
            throw new SQLTableException("get table record size failed");
        } else {
            List<Integer> ids = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ids.add(i);
            }
            return ids;
        }
    }

    /**
     * 删除所有记录
     */
    public void removeAll() throws SQLTableException {
        int fileId = SystemManager.getInstance().getFileId();
        try {
            Page page = BufPageManager.getInstance().getPage(fileId, pageId);
            TablePageUser.removeAllRecord(page);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLTableException("remove all fialed");
        }
    }

    public int getColumnCol(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].name.equals(columnName)) return i;
        }
        return -1;
    }

    public Column getColumn(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].name.equals(columnName)) return columns[i];
        }
        return null;
    }

    public int getRecordSize() {
        int fileId = SystemManager.getInstance().getFileId();
        try {
            Page page = BufPageManager.getInstance().getPage(fileId, pageId);
            return TablePageUser.getRecordSize(page);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Object[] getRecord(int index) {
        int fileId = SystemManager.getInstance().getFileId();
        try {
            Page page = BufPageManager.getInstance().getPage(fileId, pageId);
            byte[] data = TablePageUser.getRecord(page,index);
            return Record.bytesToValues(this,data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //-------------------辅助函数-------------------
    //检查是否与primary key冲突
    protected boolean checkPrimaryOk(byte[] data) throws SQLTableException {
        int fileId = SystemManager.getInstance().getFileId();
        if (fileId == -1) throw new SQLTableException("can not get DB fileId");
        try {
            Page page = BufPageManager.getInstance().getPage(fileId, pageId);
            int keyPos = TablePageUser.getPrimaryCol(page);
            if (keyPos > -1) {
                Object[] toCheck = Record.bytesToValues(this, data);
                List<Object[]> records = getAllRecords();
                for (Object[] record : records) {
                    if (record[keyPos].equals(toCheck[keyPos])) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            throw new SQLTableException("check failed: " + e.getMessage());
        }
    }

    // 记录的域名转为列位置
    protected int[] fieldsToCols(String[] fields) throws SQLTableException {
        int cols[] = new int[fields.length];
        for (int i = 0; i < cols.length; i++) {
            cols[i] = fieldToCol(fields[i]);
            if (cols[i] == -1) {
                throw new SQLTableException("not have column: " + fields[i]);
            }
        }
        return cols;
    }

    protected int fieldToCol(String field) {
        for (int j = 0; j < columns.length; j++) {
            if (field.equals(columns[j].name)) {
                return j;
            }
        }
        return -1;
    }
}
