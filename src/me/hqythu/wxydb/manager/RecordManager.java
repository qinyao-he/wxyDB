package me.hqythu.wxydb.manager;

import me.hqythu.wxydb.exception.level1.SQLRecordException;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.util.SetValue;
import me.hqythu.wxydb.util.Where;

import java.util.Arrays;
import java.util.List;


//recordLength 记录的长度
//recordSizePerPage 每一页记录的个数
//size         文件中记录总数
//fieldSize    记录的字段个数
//recordFieldPos 记录的每个字段的位置

/**
 * RecordManager
 * 记录管理器
 * 记录的插入、删除、修改
 */
public class RecordManager {

    private static RecordManager manager = null;
    private static boolean fast;

    public static RecordManager getInstance() {
        if (manager == null) {
            manager = new RecordManager();
            manager.clearFast();
        }
        return manager;
    }
    public void setFast() {fast = true;}
    public void clearFast() {fast = false;}

    /**
     * INSERT
     */
    public boolean insert(String tableName, List<Object> values) throws SQLRecordException {
        int nCol = values.size();
        Object[] mvalues = new Object[nCol];
        values.toArray(mvalues);
        insert(tableName, mvalues);
        return true;
    }

    /**
     * DELETE
     */
    public void remove(String tableName, Where where) throws SQLRecordException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLRecordException("not have table: " + tableName);
        try {
            table.remove(where);
        } catch (Exception e) {
            throw new SQLRecordException(e.getMessage());
        }
    }

    /**
     * UPDATE
     */
    public void update(String tableName, Where where, List<SetValue> setValues) throws SQLRecordException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLRecordException("not have table: " + tableName);
        try {
            table.update(where, setValues);
        } catch (Exception e) {
            throw new SQLRecordException(e.getMessage());
        }
    }

    //--------------------内部辅助函数--------------------
    protected void insert(String tableName, Object[] values) throws SQLRecordException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLRecordException("not have table: " + tableName);
        try {
            table.insert(values,fast);
        } catch (Exception e) {
            throw new SQLRecordException(e.getMessage());
        }
    }

    private RecordManager() {

    }
}

