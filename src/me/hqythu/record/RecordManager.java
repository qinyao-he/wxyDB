package me.hqythu.record;

import me.hqythu.exception.SQLExecException;
import me.hqythu.sql.Condition;
import me.hqythu.sql.SelectOption;


//recordLength 记录的长度
//recordSizePerPage 每一页记录的个数
//size         文件中记录总数
//fieldSize    记录的字段个数
//recordFieldPos 记录的每个字段的位置

public class RecordManager {

    public void insert(String tableName, Object[] values) throws SQLExecException {

    }

    public void insert(String tableName, String[] fields, Object[] values) throws SQLExecException {

    }

    public void insert(String tableName, int[] cols, Object[] values) throws SQLExecException {

    }

    public void remove(String tableName, Condition condition) throws SQLExecException {

    }

    public void update(String tableName, String[] fields, Object[] values, Condition condition) throws SQLExecException {

    }

    public void update(String tableName, int[] cols, Object[] values, Condition condition) throws SQLExecException {

    }

    public QuerySet query(String tableName, String[] fields, SelectOption option, Condition condition) throws SQLExecException {
        return null;
    }

    public QuerySet query(String tableName, int[] cols, SelectOption option, Condition condition) throws SQLExecException {
        return null;
    }

    public RecordManager() {

    }
}

