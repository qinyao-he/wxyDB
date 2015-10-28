package me.hqythu.record;

import me.hqythu.exception.SQLExecException;
import me.hqythu.system.SystemManager;
import me.hqythu.system.Table;
import me.hqythu.util.Where;
import me.hqythu.util.SelectOption;


//recordLength 记录的长度
//recordSizePerPage 每一页记录的个数
//size         文件中记录总数
//fieldSize    记录的字段个数
//recordFieldPos 记录的每个字段的位置

public class RecordManager {

    public void insert(String tableName, Object[] values) throws SQLExecException {
        Table table = SystemManager.getInstance().getTable(tableName);
        if (table == null) throw new SQLExecException(String.format("have not table %s",tableName));

    }

    public void insert(String tableName, String[] fields, Object[] values) throws SQLExecException {

    }

    public void insert(String tableName, int[] cols, Object[] values) throws SQLExecException {

    }

    public void remove(String tableName, Where where) throws SQLExecException {

    }

    public void update(String tableName, String[] fields, Object[] values, Where where) throws SQLExecException {

    }

    public void update(String tableName, int[] cols, Object[] values, Where where) throws SQLExecException {

    }

    public QuerySet query(String tableName, String[] fields, SelectOption option, Where where) throws SQLExecException {
        return null;
    }

    public QuerySet query(String tableName, int[] cols, SelectOption option, Where where) throws SQLExecException {
        return null;
    }

    public RecordManager() {

    }
}

