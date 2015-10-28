package me.hqythu.sql;

import me.hqythu.exception.SQLExecException;
import me.hqythu.util.SelectOption;
import me.hqythu.util.Where;

import java.util.function.Function;

/**
 * Created by apple on 15/10/26.
 */
public class SQL {

    enum Type {DML, DDL}

    enum DMLType {CREATE, DROP, USE, SHOW, DESC}

    enum DDLType {INSERT, DELETE, UPDATE, SELECT}

    String sql = null;

    Type type = null;
    DMLType dmlType = null;
    DDLType ddlType = null;

    // TABLE_NAME
    String tableName = null;

    // SELECT
    SelectOption selectOption = null;

    // WHERE
    Where where = null;

    // COLUMNS
    String[] fields = null;

    // VALUES
    Object[] values = null;

    // FUNCTION
    // SQL函数 AVG SUM COUNT 等等
    Function function = null;


    public SQL() {
    }

    public String execute() throws SQLExecException {
        return "hello world";
    }
}
