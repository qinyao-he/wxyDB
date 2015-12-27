package me.hqythu.wxydb.sql;

import me.hqythu.wxydb.exception.SQLExecException;
import me.hqythu.wxydb.manager.SystemManager;
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
	OrderType type;
	String dataBaseName;
	List<String> tableNames;
	List<String> rowNames;
	SelectOption selectOption;
	List<Object> data;
	List<SetValue> values;
	Where where;
	public ParseResult()
	{
		selectOption = new SelectOption();
		tableNames = new ArrayList<String>();
		rowNames = new ArrayList<String>();
		data = new ArrayList<Object>();
		values = new ArrayList<SetValue>();
	}
}
