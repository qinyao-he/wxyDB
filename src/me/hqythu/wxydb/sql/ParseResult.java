package me.hqythu.wxydb.sql;

import me.hqythu.wxydb.exception.level0.SQLExecException;
import me.hqythu.wxydb.manager.RecordManager;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Column;
import me.hqythu.wxydb.util.SelectOption;
import me.hqythu.wxydb.util.SetValue;
import me.hqythu.wxydb.util.Where;

import java.util.ArrayList;
import java.util.List;

public class ParseResult
{
	public enum OrderType
	{
		INSERT,
		DELETE,
		UPDATE,
		SELECT,
		CREATE_DATABASE,
		DROP_DATABASE,
		USE,
		SHOW_TABLES,
		CREATE_TABLE,
		DROP_TABLE,
		DESC,
		OrderType, ERROR
	}
    // ok
	public OrderType type;
	public String dataBaseName;
	public List<Object> data;           // insert
	public List<SetValue> values;       // update
	public SelectOption selectOption;   // select
	Where where;

    // 需要修改
	public List<String> tableNames;            // String tableName
    public List<Column> columns;               // create table

    // 不可见
	protected List<String> rowNames;

	public ParseResult()
	{
		selectOption = new SelectOption();
		tableNames = new ArrayList<String>();
		rowNames = new ArrayList<String>();
		data = new ArrayList<Object>();
		values = new ArrayList<SetValue>();
        columns = new ArrayList<>();
	}
    public String execute() throws SQLExecException {
        String result = "error";
        Object[] objects;
        boolean ok;
        try {
            switch (type) {
                case INSERT:
                    ok = RecordManager.getInstance().insert(tableNames.get(0),data);
                    if (ok) {
                        result = "insert success";
                    } else {
                        result = "insert failed";
                    }
                    break;
                case DELETE:
                    break;
                case UPDATE:
                    break;
                case SELECT:
                    break;
                case CREATE_DATABASE:
                    ok = SystemManager.getInstance().createDatabase(dataBaseName);
                    if (ok) {
                        result = "create database " + dataBaseName + " success";
                    } else {
                        result = "create database " + dataBaseName + " failed";
                    }
                    break;
                case DROP_DATABASE:
                    ok = SystemManager.getInstance().dropDatabase(dataBaseName);
                    if (ok) {
                        result = "drop database " + dataBaseName + " success";
                    } else {
                        result = "drop database " + dataBaseName + " failed";
                    }
                    break;
                case USE:
                    ok = SystemManager.getInstance().useDatabase(dataBaseName);
                    if (ok) {
                        result = "use database " + dataBaseName + " success";
                    } else {
                        result = "use database " + dataBaseName + " failed";
                    }
                    break;
                case SHOW_TABLES:
                    result = SystemManager.getInstance().showTables();
                    break;
                case CREATE_TABLE:
                    ok = SystemManager.getInstance().createTable(tableNames.get(0),columns);
                    if (ok) {
                        result = "create table " + tableNames.get(0) + " success";
                    } else {
                        result = "create table " + tableNames.get(0) + " failed";
                    }
                    break;
                case DROP_TABLE:
                    break;
                case DESC:
                    break;
                case ERROR:
                    result = "parse sql error";
                    break;
            }
        } catch (Exception e) {
            throw new SQLExecException(e.getMessage());
        }

        return result;
    }
}
