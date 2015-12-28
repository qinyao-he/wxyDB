package me.hqythu.wxydb.sql;

import me.hqythu.wxydb.sql.Node.Type;
import me.hqythu.wxydb.util.BoolExpr;
import me.hqythu.wxydb.util.BoolOp;
import me.hqythu.wxydb.util.CompareOp;
import me.hqythu.wxydb.util.Where;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ConditionParser
{
	static List<Node> resultList;
	static Object exchange(String value)
	{
		Object resultObject;
		value = value.replaceAll("\\,", "");
		value = value.replaceAll("\\)", "");
		value = value.replaceAll("\\;", "");
		if (value.startsWith("\'") || value.startsWith("��") || value.startsWith("��"))
		{
			value = value.replaceAll("\\'", "");
			value = value.replaceAll("\\��", "");
			value = value.replaceAll("\\��", "");
			resultObject = value;
		}
		else if (value.contains(".") || value.toUpperCase().equals("NULL"))
		{
			resultObject = '*'+value;
		}
		else
		{
            try
            {
                resultObject = Integer.valueOf(value);
            }
            catch (NumberFormatException e)
            {
                resultObject = "*" + value;
            }
		}
		return resultObject;
	}
	public static void main(String[] args)
	{
		String string = "authors=��Anthony Boucher��;";
        Where result = parseCondition(string, "myTable");
        for (Object obj : result.boolExprsAndOps)
		{
			System.out.println(obj);
		}
        for (Boolean bool : result.isExprs)
		{
			System.out.println(bool);
		}
	}
	static DoubleReturn<Condition, String> String2Condition(String sql)
	{
		sql = sql.trim();
		Condition result = new Condition();
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
			if (isSym(sql.charAt(i)) || (i > 0 && isIS(sql, i)) || (sql.charAt(i) == ' '))
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
	static void post(Node currentNode)
	{
		if (currentNode == null)
		{
			return;
		}
		post(currentNode.leftChild);
		post(currentNode.rightChild);
		resultList.add(currentNode);
	}
	static Where parseCondition(String sql, String tableName)
	{
		resultList = new ArrayList<Node>();
		List<Boolean> isExprs = new ArrayList<Boolean>();
		List<Object> boolExprsAndOps = new ArrayList<Object>();
//		System.out.println(sql);
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
				boolean end = false;
				while(!opr.isEmpty() && rank.get(currentString) <= rank.get(opr.lastElement()))
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
						end = true;
						break;
					}
				}
				if (sql.charAt(i) != ')')
				{
					opr.push(currentString);
				}
				if (end)
				{
					break;
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
		post(opt.firstElement());
        for (Node node: resultList)
        {
            if (node.type == Type.SEP)
            {
                BoolOp op;
                switch(node.sep)
                {
                    case "AND":
                        op = BoolOp.AND;
                        boolExprsAndOps.add(op);
                        break;
                    case "OR":
                        op = BoolOp.OR;
                        boolExprsAndOps.add(op);
                        break;
                }
                isExprs.add(false);
            }
            else
            {
                BoolExpr expr = new BoolExpr();
                if (!node.condition.left.contains("."))
                {
                    expr.tableNameL = tableName;
                    expr.columnNameL = node.condition.left;
                }
                else
                {
                    expr.tableNameL = node.condition.left.substring(0, node.condition.left.indexOf('.'));
                    expr.columnNameL = node.condition.left.substring(node.condition.left.indexOf('.')+1);
                }
                switch (node.condition.middle) {
                    case ">":
                        expr.compareOp = CompareOp.GTR;
                        break;
                    case "<":
                        expr.compareOp = CompareOp.LES;
                        break;
                    case ">=":
                        expr.compareOp = CompareOp.GEQ;
                        break;
                    case "<=":
                        expr.compareOp = CompareOp.LEQ;
                        break;
                    case "<>":
                        expr.compareOp = CompareOp.NEQ;
                        break;
                    case "is":
                        expr.compareOp = CompareOp.IS;
                        break;
                }
                if (node.condition.right instanceof String)
                {
                    String right = (String)node.condition.right;
                    if (!right.contains("."))
                    {
                        if (right.startsWith("*"))
                        {
                            expr.tableNameR = tableName;
                            expr.columnNameR = right.substring(1);
                        }
                        else
                        {
                            expr.valueR = node.condition.right;
                        }
                    }
                    else
                    {
                        expr.tableNameR = right.substring(0, node.condition.left.indexOf('.')-1);
                        expr.columnNameR = right.substring(node.condition.left.indexOf('.'));
                    }
                }
                else
                {
                    expr.valueR = node.condition.right;
                }
                boolExprsAndOps.add(expr);
                isExprs.add(true);
            }
        }
        Where result = new Where();
        result.boolExprsAndOps = boolExprsAndOps;
        result.isExprs = isExprs;
		return result;
	}
}
