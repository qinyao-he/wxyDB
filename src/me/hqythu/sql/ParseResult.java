package me.hqythu.sql;

import me.hqythu.exception.SQLExecException;

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
        return "hello world";
    }
}
