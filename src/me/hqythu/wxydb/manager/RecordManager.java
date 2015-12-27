package me.hqythu.wxydb.manager;

import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.exception.SQLExecException;
import me.hqythu.wxydb.exception.SQLRecordException;
import me.hqythu.wxydb.exception.SQLTableException;
import me.hqythu.wxydb.util.SetValue;
import me.hqythu.wxydb.util.Where;

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

    public static RecordManager getInstance() {
        if (manager == null) {
            manager = new RecordManager();
        }
        return manager;
    }

    public void insert(String tableName, List<String> fields, List<Object> values) throws SQLTableException {
        int nCol = fields.size();
        String[] mfields = new String[nCol];
        Object[] mvalues = new Object[nCol];
        fields.toArray(mfields);
        values.toArray(mvalues);
        insert(tableName,mfields,mvalues);
    }

    public void insert(String tableName, List<Object> values) throws SQLTableException {
        int nCol = values.size();
        Object[] mvalues = new Object[nCol];
        values.toArray(mvalues);
        insert(tableName,mvalues);
    }

    protected void insert(String tableName, String[] fields, Object[] values) throws SQLTableException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLTableException("not have table: " + tableName);
        table.insert(fields, values);
    }

    protected void insert(String tableName, Object[] values) throws SQLTableException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLTableException("not have table: " + tableName);
        table.insert(values);
    }

    public void remove(String tableName, Where where) throws SQLExecException, SQLTableException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLExecException("not have table: " + tableName);
        table.remove(where);
    }

    public void update(String tableName, Where where, List<SetValue> setValues) throws SQLExecException, SQLTableException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLExecException("not have table: " + tableName);
        table.update(where, setValues);
    }

    private RecordManager() {

    }
}

