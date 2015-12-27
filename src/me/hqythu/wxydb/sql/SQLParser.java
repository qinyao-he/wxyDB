package me.hqythu.wxydb.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Exchanger;

import javax.sound.midi.MidiChannel;






//import query.Condition.Type;
import me.hqythu.wxydb.util.CalcOp;
import me.hqythu.wxydb.util.SelectOption;
import me.hqythu.wxydb.util.SetValue;
import  me.hqythu.wxydb.sql.ParseResult.OrderType;

public class SQLParser
{
	static Object exchange(String value)
	{
		Object resultObject;
		value = value.replaceAll("\\,", "");
		value = value.replaceAll("\\)", "");
		value = value.replaceAll("\\;", "");
		if (value.startsWith("\'") || value.startsWith("‘") || value.startsWith("’"))
		{
			value = value.replaceAll("\\'", "");
			value = value.replaceAll("\\‘", "");
			value = value.replaceAll("\\’", "");
			resultObject = value;
		}
		else if (value.contains("."))
		{
			resultObject = value;
		}
		else
		{
			if (isNumStm(value))
			{
				resultObject = calcStm(value);
			}
			else
			{
				SetValue v = new SetValue();
				value = value.trim();
				int statue = 0;
				while(value.length() > 0)
				{
					if (statue == 0)
					{
						if (isNum(value.charAt(0)))
						{
							DoubleReturn<Integer, String> result = readNum(value);
							value = result.second;
							v.isVar1 = false;
							v.value1 = result.first;
							statue = 1;
						}
						else
						{
							v.isVar1 = true;
							value = readVar(value);
							statue = 1;
						}
					}
					else if (statue == 1)
					{
						value = value.trim();
						switch (value.charAt(0))
						{
							case '+':
								v.calcOp = CalcOp.ADD;
								break;
							case '-':
								v.calcOp = CalcOp.SUB;
								break;
							case '*':
								v.calcOp = CalcOp.MUL;
								break;
							case '/':
								v.calcOp = CalcOp.DIV;
								break;
							default:
								break;
						}
						value = value.substring(1);
						value = value.trim();
						statue = 2;
					}
					else if (statue == 2)
					{
						if (isNum(value.charAt(0)))
						{
							DoubleReturn<Integer, String> result = readNum(value);
							value = result.second;
							v.isVar2 = false;
							v.value2 = result.first;
							break;
						}
						else
						{
							v.isVar2 = true;
							value = readVar(value);
							break;
						}
					}
				}
				resultObject = v;
			}
		}
		return resultObject;
	}
	static DoubleReturn<String, String> readString(String sql)
	{
		String var = "";
		for (int i = 0; i < sql.length(); i++)
		{
			if (sql.charAt(i) == ' ')
			{
				sql = sql.substring(i);
				break;
			}
			else
			{
				var += sql.charAt(i);
			}
		}
		return new DoubleReturn<String, String>(var, sql);
	}
	static String readVar(String sql)
	{
		for (int i = 0; i < sql.length(); i++)
		{
			if (isSym(sql.charAt(i)))
			{
				sql = sql.substring(i);
				break;
			}
		}
		return sql;
	}
	static Integer calcStm(String str)
	{
//		System.out.println(str);
		Stack<Integer> opr = new Stack<Integer>();
		Stack<String> ins = new Stack<String>();
		ins.push("#");
		Map<String, Integer> rank = new HashMap<String, Integer>();
		str += '#';
		rank.put("#", -2);
		rank.put("(", -1);
		rank.put("+", 0);
		rank.put("-", 0);
		rank.put("*", 1);
		rank.put("/", 1);
		rank.put(")", -1);
		while(str.length() > 0)
		{
			Integer num = 0;
			if (isNum(str.charAt(0)))
			{
				DoubleReturn<Integer, String> result = new DoubleReturn<Integer, String>();
				result = readNum(str);
				num = result.first;
				str = result.second;
				opr.push(num);
			}
			else
			{
				if (rank.get(String.valueOf(str.charAt(0))) <= rank.get(ins.lastElement()))
				{
					if (ins.lastElement().equals("#") && str.charAt(0) == '#')
					{
						break;
					}
					Integer a = opr.pop();
					Integer b = opr.pop();
					Integer r = 0;
					String i = ins.pop();
					switch (i)
					{
						case "+":
							r = a + b;
							break;
						case "-":
							r = a - b;
							break;
						case "*":
							r = a * b;
							break;
						case "/":
							r = a / b;
							break;
						default:
							break;
					}
					opr.push(r);
					if (str.charAt(0) == ')')
					{
						ins.pop();
					}
					else
					{
						ins.push(String.valueOf(str.charAt(0)));
					}
					str = str.substring(1);
				}
				else
				{
					ins.push(String.valueOf(str.charAt(0)));
					str = str.substring(1);
				}
			}
		}
		return opr.firstElement();
	}
	static boolean isNumStm(String sql)
	{
		for (int i = 0; i < sql.length(); i++)
		{
			if (!(isNum(sql.charAt(i)) || isSym(sql.charAt(i))))
			{
				return false;
			}
		}
		return true;
	}
	static boolean isNum(char c)
	{
		if (c >= '0' && c <= '9')
		{
			return true;
		}
		return false;
	}
	static boolean isSym(char c)
	{
		return (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')');
	}
	static DoubleReturn<Integer, String> readNum(String str)
	{
		String num = "";
		while(str.length() > 0 && isNum(str.charAt(0)))
		{
			num += str.charAt(0);
			str = str.substring(1);
		}
		DoubleReturn<Integer, String> result = new DoubleReturn<Integer, String>();
		result.first = Integer.valueOf(num);
		result.second = str;
		return result;
	}
//	static List<Condition> parseConditions(List<String> conds)
//	{
//		List<Condition> conditions = new ArrayList<Condition>();
//		int num = 0;
//		Condition condition = new Condition();
//		String value = "";
//		for(int i = 0; i < conds.size()-1; i++)
//		{
//			if (num == 0)
//			{
//				condition.left = conds.get(i);
//				num = 1;
//				continue;
//			}
//			else if (num == 1)
//			{
//				String middle = conds.get(i);
//				if (conds.get(i+1).equals(">") || conds.get(i+1).equals("="))
//				{
//					middle += conds.get(i+1);
//					i++;
//				}
//				condition.middle = middle;
//				num = 2;
//				continue;
//			}
//			else
//			{
//				if (conds.get(i).endsWith("\'") || conds.get(i).endsWith("��") || conds.get(i).endsWith("��") || conds.get(i+1).equals(";") || conds.get(i+1).toUpperCase().equals("AND") ||conds.get(i+1).toUpperCase().equals("OR") )
//				{
//					String v = conds.get(i);
//					value += v;
//					condition.right = exchange(value);
//					value = "";
//					condition.type = Type.COND;
//					conditions.add(condition);
//					condition = new Condition();
//					if (conds.get(i+1).toUpperCase().equals("AND") || conds.get(i+1).toUpperCase().equals("OR"))
//					{
//						condition.type = Type.SEP;
//						condition.sep = conds.get(i+1).toUpperCase();
//						conditions.add(condition);
//						condition = new Condition();
//						i++;
//					}
//					num = 0;
//				}
//				else
//				{
//					value = conds.get(i);
//					value += ' ';
//				}
//			}
//		}
//		return conditions;
//	}
	static ParseResult parseINSERT(String sqlString)
	{
		sqlString = sqlString.replaceAll(" \\, ", ", ");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		String currentString = "";
		for (int i = 4; i < sql.length; i++)
		{
			sql[i] = sql[i].replaceAll("\\(", "");
			sql[i] = sql[i].replaceAll("\\)", "");
			currentString += sql[i];
			if (currentString.endsWith(",") || currentString.endsWith(";"))
			{
				result.data.add(exchange(currentString));
				currentString = "";
			}
			else 
			{
				currentString += ' ';
			}
		}
		result.type = OrderType.INSERT;
		result.tableNames.add(sql[2]);
		return result;
	}
	static ParseResult parseDELETE(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		result.tableNames.add(sql[2]);
		String conds = "";
		for (int i = 4; i < sql.length; i++)
		{
			if (sql[i].isEmpty())
			{
				continue;
			}
			conds += sql[i]+' ';
		}
		result.type = OrderType.DELETE;
		result.where = ConditionParser.parseCondition(conds, result.tableNames.get(0));
		return result;
	}
	static ParseResult parseUPDATE(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		result.tableNames.add(sql[1]);
		int num = 0;
		Value value = new Value();
		String right = "";
		boolean startCopy = false;
		String conds = "";
		for(int i = 3; i < sql.length; i++)
		{
			if (sql[i].toUpperCase().equals("WHERE"))
			{
				startCopy = true;
				continue;
			}
			if (startCopy)
			{
				conds += sql[i]+' ';
			}
			else
			{
				if (num == 0)
				{
					value.left = sql[i];
					i++;
					num = 1;
					continue;
				}
				else if (num == 1) 
				{
					if (sql[i+1].equals(",") || sql[i+1].toUpperCase().equals("WHERE"))
					{
						right += sql[i];
						value.right = exchange(right);
						SetValue v = new SetValue();
						if (!(value.right instanceof SetValue))
						{
							v.isVar1 = false;
							v.value1 = value.right;
						}
						else
						{
							v = (SetValue)value.right;
						}
						v.columnName = value.left;
						result.values.add(v);
						value = new Value();
						num = 0;
						if (sql[i+1].equals(","))
						{
							i++;
						}
						right = "";
					}
					else
					{
						right += sql[i];
						right += ' ';
					}
				}
			}
		}
		result.type = OrderType.UPDATE;
		result.where = ConditionParser.parseCondition(conds, result.tableNames.get(0));
		return result;
	}
	static ParseResult parseSELECT(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		int num = 0;
		String conds = "";
		for (int i = 1; i < sql.length; i++)
		{
			if (num == 0)
			{
				if (sql[i].toUpperCase().equals("FROM"))
				{
					num = 1;
					continue;
				}
				else if (!sql[i].equals(","))
				{
					result.rowNames.add(sql[i]);
				}
			}
			else if (num == 1)
			{
				if (sql[i].toUpperCase().equals("WHERE"))
				{
					num = 2;
					continue;
				}
				else if (!sql[i].equals(","))
				{
					result.tableNames.add(sql[i]);
				}
				
			}
			else 
			{
				conds += sql[i]+' ';
			}
		}
		if (result.rowNames.size() == 1 && result.rowNames.get(0).equals("*"))
		{
			result.selectOption = new SelectOption(true);
		}
		else
		{
			for (String row : result.rowNames)
			{
				if (row.contains("."))
				{
					result.selectOption.add(row.substring(0, row.indexOf(".")), row.substring(row.indexOf(".")+1));
				}
				else
				{
					result.selectOption.add(result.tableNames.get(0), row);
				}
			}
		}
		for (String table : result.tableNames)
		{
			result.selectOption.addFromTable(table);
		}
		result.type = OrderType.SELECT;
		result.where = ConditionParser.parseCondition(conds, result.tableNames.get(0));
		result.tableNames = new ArrayList<String>();
		result.rowNames = new ArrayList<String>();
		return result;
	}
	static ParseResult parseCREATE_DATABASE(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		result.type = OrderType.CREATE_DATABASE;
		result.dataBaseName = sql[2];
		return result;
	}
	static ParseResult parseDROP_DATABASE(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		result.type = OrderType.DROP_DATABASE;
		result.dataBaseName = sql[2];
		return result;
	}
	static ParseResult parseUSE(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		result.type = OrderType.USE;
		result.dataBaseName = sql[1];
		return result;
	}
	static ParseResult parseSHOW_TABLES(String sqlString)
	{
		ParseResult result = new ParseResult();
		result.type = OrderType.SHOW_TABLES;
		return result;
	}
	static ParseResult parseDROP_TABLE(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		result.type = OrderType.DROP_TABLE;
		result.tableNames.add(sql[2]);
		return result;
	}
	static ParseResult parseDESC(String sqlString)
	{
		sqlString = sqlString.replaceAll(";", " ;");
		String sql[] =  sqlString.split(" ");
		ParseResult result = new ParseResult();
		result.type = OrderType.DESC;
		result.tableNames.add(sql[1]);
		return result;
	}
//	static ParseResult parseCREATE_TABLE(String sqlString)
//	{
//		sqlString = sqlString.replaceAll(";", " ;");
//		sqlString = sqlString.replaceAll("\\( ", "(");
//		sqlString = sqlString.replaceAll(" \\(", "(");
//		sqlString = sqlString.replaceAll("\\(", " ( ");
//		sqlString = sqlString.replaceAll("\\) ", ")");
//		sqlString = sqlString.replaceAll(" \\)", ")");
//		sqlString = sqlString.replaceAll("\\)", " ) ");
//		String sql[] =  sqlString.split(" ");
//		ParseResult result = new ParseResult();
//		result.tableNames.add(sql[2]);
//		sqlString = "";
//		for (int i = 2; i < sql.length; i++)
//		{
//			sqlString += sql[i] + ' ';
//		}
//		System.out.println(sqlString);
////		return null;
//		int status = 0;
//		while(sqlString.length() > 0)
//		{
//			if (status == 0)
//			{
//				sqlString = sqlString.trim();
//				DoubleReturn<String, String> r = readString(sqlString);
//				result.tableNames.add(r.first);
//				sqlString = r.second;
//				status = -1;
//			}
//			else if (status == -1)
//			{
//				sqlString = sqlString.trim();
//				sqlString = sqlString.substring(1);
//				status = 1;
//			}
//		}
//	}
	public static void main(String[] args)
	{
//		Scanner s = new Scanner(System.in);
//		String sql = s.nextLine();
		ParseResult result = handleSQL("CREATE TABLE customer(\n"+
										"id int(10) NOT NULL,\n"+
										"name varchar(25) NOT NULL,\n"+
										"gender varchar(1) NOT NULL,\n"+
										"PRIMARY KEY(id)\n"+
										");");
//		System.out.print("123");
	}
	static ParseResult handleSQL(String sql)
	{
		sql = sql.replaceAll("\\, ", ",");
		sql = sql.replaceAll(" \\,", ",");
		sql = sql.replaceAll("\\,", " , ");
		sql = sql.replaceAll("= ", "=");
		sql = sql.replaceAll(" =", "=");
		sql = sql.replaceAll("=", " = ");
		sql = sql.replaceAll("< ", "<");
		sql = sql.replaceAll(" <", "<");
		sql = sql.replaceAll("<", " < ");
		sql = sql.replaceAll("> ", ">");
		sql = sql.replaceAll(" >", ">");
		sql = sql.replaceAll(">", " > ");
		String ss[] =  sql.split(" ");
		ParseResult result = new ParseResult();
		try
		{
			if (ss[0].toUpperCase().equals("INSERT"))
			{
				result = parseINSERT(sql);
			}
			else if (ss[0].toUpperCase().equals("DELETE")) 
			{
				result = parseDELETE(sql);
			}
			else if (ss[0].toUpperCase().equals("UPDATE"))
			{
				result = parseUPDATE(sql);
			}
			else if (ss[0].toUpperCase().equals("SELECT"))
			{
				result = parseSELECT(sql);
			}
			else if (ss[0].toUpperCase().equals("CREATE") && ss[1].toUpperCase().equals("DATABASE"))
			{
				result = parseCREATE_DATABASE(sql);
			}
			else if (ss[0].toUpperCase().equals("DROP") && ss[1].toUpperCase().equals("DATABASE"))
			{
				result = parseDROP_TABLE(sql);
			}
			else if (ss[0].toUpperCase().equals("USE"))
			{
				result = parseUSE(sql);
			}
			else if (ss[0].toUpperCase().equals("SHOW") && ss[1].toUpperCase().equals("TABLES;"))
			{
				result = parseSHOW_TABLES(sql);
			}
//			else if (ss[0].toUpperCase().equals("CREATE") && ss[1].toUpperCase().equals("TABLE"))
//			{
//				result = parseCREATE_TABLE(sql);
//			}
			else if (ss[0].toUpperCase().equals("DROP") && ss[1].toUpperCase().equals("TABLE"))
			{
				result = parseDROP_TABLE(sql);
			}
			else if (ss[0].toUpperCase().equals("DESC"))
			{
				result = parseDESC(sql);
			}
			else
			{
				result.type = OrderType.ERROR;
			}
		} catch (Exception e)
		{
			result.type = OrderType.ERROR;
		}
		return result;
	}
	
}
