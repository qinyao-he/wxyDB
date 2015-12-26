package me.hqythu.wxydb.sql;

import java.util.HashMap;
import java.util.Stack;

import me.hqythu.wxydb.sql.Node.Type;
class DoubleReturn<T1,T2>
{
	T1 first;
	T2 second;
}
public class ConditionParser
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
		else if (value.contains(".") || value.toUpperCase().equals("NULL"))
		{
			resultObject = value;
		}
		else
		{
			resultObject = Integer.valueOf(value);
		}
		return resultObject;
	}
	public static void main(String[] args)
	{
		String string = "authors=’Anthony Boucher’ and state<=100 AND book.cde <> 'acb' OR abc > 123;";
		Node root = parseCondition(string);
	}
	static DoubleReturn<Condition, String> String2Condition(String sql)
	{
		sql = sql.trim();
		String left = "";
		Condition result = new Condition();
		int num = 0;
		DoubleReturn<String, String> readResult = readID(sql);
		result.left = readResult.first;
		sql = readResult.second;
		readResult = readSym(sql);
		result.middle = readResult.first;
		sql = readResult.second;
		readResult = readValue(sql);
		result.right = exchange(readResult.first);
		sql = readResult.second;
		DoubleReturn<Condition, String> ret = new DoubleReturn<Condition, String>();
		ret.first = result;
		ret.second = sql;
		return ret;
	}
	static boolean isSym(char s)
	{
		if (s=='<'||s=='>'||s=='=')
		{
			return true;
		}
		return false;
	}
	static DoubleReturn<String, String> readID(String sql)
	{
		sql = sql.trim();
		String resultString = "";
		for (int i = 0; i < sql.length(); i++)
		{
			if (isSym(sql.charAt(i)) || (i > 0 && isIS(sql, i)))
			{
				sql = sql.substring(i);
				break;
			}
			resultString += sql.charAt(i);
		}
		DoubleReturn<String, String> result = new DoubleReturn<String, String>();
		result.first = resultString;
		result.second = sql;
		return result;
	}
	static DoubleReturn<String, String> readSym(String sql)
	{
		sql = sql.trim();
		String resultString = "";
		for (int i = 0; i < sql.length(); i++)
		{
			if (isIS(sql, i))
			{
				resultString = "is";
				sql = sql.substring(i+2);
				break;
			}
			if (sql.charAt(i) == ' ')
			{
				continue;
			}
			if (!isSym(sql.charAt(i)))
			{
				sql = sql.substring(i);
				break;
			}
			resultString += sql.charAt(i);
		}
		DoubleReturn<String, String> result = new DoubleReturn<String, String>();
		result.first = resultString;
		result.second = sql;
		return result;
	}
	static DoubleReturn<String, String> readValue(String sql)
	{
		sql = sql.trim();
		String resultString = "";
		for (int i = 0; i < sql.length(); i++)
		{
			resultString += sql.charAt(i);
		}
		DoubleReturn<String, String> result = new DoubleReturn<String, String>();
		result.first = resultString;
		result.second = sql;
		return result;
	}
	static boolean isAND(String sql, int i)
	{
		if (sql.substring(i).toUpperCase().startsWith("AND") && sql.substring(i).charAt(3) == ' ')
		{
			return true;
		}
		return false;
	}
	static boolean isOR(String sql, int i)
	{
		if (sql.substring(i).toUpperCase().startsWith("OR") && sql.substring(i).charAt(2) == ' ')
		{
			return true;
		}
		return false;
	}
	static boolean isIS (String sql, int i)
	{
		if (sql.substring(i).toUpperCase().startsWith("IS") && sql.substring(i).charAt(2) == ' ')
		{
			return true;
		}
		return false;
	}
	static DoubleReturn<Condition, String> readCondition(String sql)
	{
		sql = sql.trim();
		String resultString = "";
		for (int i = 0; i < sql.length(); i++)
		{
			if (isAND(sql, i) || isOR(sql, i) || sql.charAt(i) == ';' || sql.charAt(i) == ')')
			{
				sql = sql.substring(i);
				break;
			}
			resultString += sql.charAt(i);
		}
		DoubleReturn<Condition, String> ret = String2Condition(resultString);
		ret.second = sql;
		return ret;
	}
	static Node parseCondition(String sql)
	{
		System.out.println(sql);
		HashMap<String, Integer> rank = new HashMap<String, Integer>();
		rank.put("OR", 0);
		rank.put("AND", 1);
		rank.put("(", 2);
		rank.put(")", -1);
		rank.put(";", -2);
		Stack<Node> opt = new Stack<Node>();
		Stack<String> opr = new Stack<String>();
		opr.push(";");
		while(sql.length() > 0)
		{
			int i = 0;
			String currentString = new String();
			if (isAND(sql, i))
			{
				currentString = "AND";
				sql = sql.substring(3);
			}
			else if (isOR(sql, i))
			{
				currentString = "OR";
				sql = sql.substring(2);
			}
			else if (sql.charAt(i) == '(' || sql.charAt(i) == ')' || sql.charAt(i) == ';' )
			{
				currentString += sql.charAt(i);
			}
			if (currentString.isEmpty())
			{
				Node node = new Node();
				node.type = Type.COND;
				DoubleReturn<Condition, String> ret = readCondition(sql);
				node.condition = ret.first;
				sql = ret.second;
				opt.push(node);
			}
			else if (sql.charAt(i) == '(' || opr.lastElement().equals("("))
			{
				opr.push(currentString);
			}
			else if (currentString.equals(";") && opr.size() == 1 && opt.size() == 1)
			{
				break;
			}
			else if (!currentString.isEmpty() && rank.get(currentString) <= rank.get(opr.lastElement())) 
			{
				String r = opr.pop();
				Node b = opt.pop();
				Node a = opt.pop();
				if (sql.charAt(i) == ')')
				{
					opr.pop();
				}
				Node node = new Node();
				node.type = Type.SEP;
				node.sep = r;
				node.leftChild = a;
				node.rightChild = b;
				opt.push(node);
				if (sql.charAt(i) == ';')
				{
					break;
				}
				if (sql.charAt(i) != ')')
				{
					opr.push(currentString);
				}
			}
			else
			{
				opr.push(currentString);
			}
			if (currentString.length() == 1)
			{
				sql = sql.substring(1);
			}
			sql = sql.trim();
		}
		return opt.firstElement();
	}
}
