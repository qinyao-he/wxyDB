package me.hqythu.wxydb.sql;

import me.hqythu.wxydb.exception.SQLExecException;
import me.hqythu.wxydb.manager.SystemManager;

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
		ERROR
	}
	OrderType type;
	String dataBaseName;
	List<String> tableNames;
	List<String> rowNames;
	List<Object> data;
	List<Value> values;
	Node conditions;
	public ParseResult()
	{
		tableNames = new ArrayList<String>();
		rowNames = new ArrayList<String>();
		data = new ArrayList<Object>();
		values = new ArrayList<Value>();
	}
    public String execute() throws SQLExecException
    {
        String result = "hello world";
        Object[] objects;
        boolean ok;
        switch (type) {
            case INSERT:
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
                    result = "create database "+dataBaseName+" success";
                } else {
                    result = "create database "+dataBaseName+" failed";
                }
                break;
            case DROP_DATABASE:
                ok = SystemManager.getInstance().dropDatabase(dataBaseName);
                if (ok) {
                    result = "drop database "+dataBaseName+" success";
                } else {
                    result = "drop database "+dataBaseName+" failed";
                }
                break;
            case USE:
                ok = SystemManager.getInstance().useDatabase(dataBaseName);
                if (ok) {
                    result = "use database "+dataBaseName+" success";
                } else {
                    result = "use database "+dataBaseName+" failed";
                }
                break;
            case SHOW_TABLES:
                result = SystemManager.getInstance().showTables();
                break;
            case CREATE_TABLE:
                break;
            case DROP_TABLE:
                break;
            case DESC:
                break;
            case ERROR:
                result = "parse sql error";
                break;
        }
        return result;
    }
}
