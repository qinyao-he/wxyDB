package me.hqythu.wxydb.sql;

import me.hqythu.wxydb.object.Column;
import me.hqythu.wxydb.object.DataType;
import me.hqythu.wxydb.sql.ParseResult.OrderType;
import me.hqythu.wxydb.util.*;

import java.util.*;

//import query.Condition.Type;

public class SQLParser
{
    static Object exchange(String value)
    {
        Object resultObject;
//        value = value.replaceAll("\\,", "");
//        value = value.replaceAll("\\)", "");
//        value = value.replaceAll("\\;", "");
//        System.out.println(value);
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
    static DoubleReturn<Func, String> readFunc(String sql)
    {
        Func func;
        if (sql.startsWith("SUM"))
        {
            func = Func.SUM;
        }
        else if (sql.startsWith("AVG"))
        {
            func = Func.AVG;
        }
        else if (sql.startsWith("MAX"))
        {
            func = Func.MAX;
        }
        else if (sql.startsWith("MIN"))
        {
            func = Func.MIN;
        }
        else
        {
            func = Func.COUNT;
        }
        sql = sql.substring(4,sql.length()-1);
        return new DoubleReturn<Func, String>(func, sql);
    }
    static DoubleReturn<DataType, String> readType(String sql)
    {
        DoubleReturn<DataType, String> result = new DoubleReturn<DataType, String>();
        if (sql.toUpperCase().startsWith("INT") && sql.charAt(3) == ' ')
        {
            result.first = DataType.INT;
            result.second = sql.substring(3);
        }
        else if (sql.toUpperCase().startsWith("VARCHAR") && sql.charAt(7) == ' ')
        {
            result.first = DataType.VARCHAR;
            result.second = sql.substring(7);
        }
        return result;
    }
    static DoubleReturn<Column, String> readColumn(String sql)
    {
        String id = "";
        DataType type = DataType.UNKNOWN;
        Integer length = 0;
        boolean notNull = false;
        int status = 0;
        sql = sql.trim();
        while(sql.length() > 0)
        {
            if (status == 0)
            {
                if (sql.charAt(0) == ' ')
                {
                    sql = sql.trim();
                    status = 1;
                }
                else
                {
                    id += sql.charAt(0);
                    sql = sql.substring(1);
                }
            }
            else if (status == 1)
            {
                sql = sql.trim();
                DoubleReturn<DataType, String> r = readType(sql);
                type = r.first;
                sql = r.second;
                status = 2;
            }
            else if (status == 2)
            {
                sql = sql.trim();
                sql = sql.substring(1);
                status = 3;
            }
            else if (status == 3)
            {
                sql = sql.trim();
                DoubleReturn<Integer, String> r = readNum(sql);
                length = r.first;
                sql = r.second;
                status = 4;
            }
            else if (status == 4)
            {
                sql = sql.trim();
                sql = sql.substring(1);
                status = 5;
            }
            else if (status == 5)
            {
                sql = sql.trim();
                if (sql.toUpperCase().startsWith("NOT NULL"))
                {
                    notNull = true;
                    sql = sql.substring(8);
                    sql = sql.trim();
                    sql = sql.substring(1);
                    sql = sql.trim();
                    break;
                }
                else
                {
                    sql = sql.trim();
                    sql = sql.substring(1);
                    sql = sql.trim();
                    notNull = false;
                    break;
                }
            }
        }
        Column column = new Column(id, type, length.shortValue());
        if (notNull)
        {
            column.setNotNull();
        }
        return new DoubleReturn<Column, String>(column, sql);
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
    static boolean isFunc(String str)
    {
        return str.toUpperCase().startsWith("SUM") || str.toUpperCase().startsWith("AVG") || str.toUpperCase().startsWith("MAX") || str.toUpperCase().startsWith("MIN") || str.toUpperCase().startsWith("COUNT");
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
//				if (conds.get(i).endsWith("\'") || conds.get(i).endsWith("‘") || conds.get(i).endsWith("’") || conds.get(i+1).equals(";") || conds.get(i+1).toUpperCase().equals("AND") ||conds.get(i+1).toUpperCase().equals("OR") )
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
    static ParseResult parseINSERT(String sql)
    {
//        System.out.println(sql);
//        return null;
        String sqlS[] =  sql.split(" ");
        ParseResult result = new ParseResult();
        result.tableNames.add(sqlS[2]);
        sql = sql.substring(sql.toUpperCase().indexOf("VALUES"));
//        for (int i = 3; i < sqlS.length; i++)
//        {
//            sql += sqlS[i] + ' ';
//        }
        sql = sql.substring(0, sql.length() - 1);
//        System.out.println(sql);
        int status = -1;
        String valueString = "";
        Object value = null;
        boolean reading = false;
        while(sql.length() > 0)
        {
            if (status == -1)
            {
                if (sql.charAt(0) == '(' || sql.charAt(0) == ' ')
                {
                    status = 0;
                }
                sql = sql.substring(1);
            }
            else if (status == 0)
            {
                if (sql.charAt(0) == '(' && !reading)
                {
                    sql = sql.substring(1);
                }
                else if (sql.trim().toUpperCase().startsWith("NULL") && !reading)
                {
                    sql = sql.trim();
                    sql = sql.substring(4);
                    sql = sql.trim();
                    if (sql.startsWith(","))
                    {
                        sql = sql.substring(1);
                    }
                    result.data.add(null);
                }
                else if (sql.charAt(0) == '\'' || sql.charAt(0) == '‘' || sql.charAt(0) == '’')
                {
                    reading = !reading;
                    valueString += sql.charAt(0);
                    sql = sql.substring(1);
                }
                else if (sql.charAt(0) == ')' && !reading)
                {
                    value = exchange(valueString.trim());
                    result.data.add(value);
                    break;
                }
                else if ((sql.charAt(0) == ',' || sql.charAt(0) == '，') && !reading)
                {
                    value = exchange(valueString.trim());
                    result.data.add(value);
                    value = null;
                    valueString = "";
                    sql = sql.substring(1);
                }
                else
                {
                    valueString += sql.charAt(0);
                    sql = sql.substring(1);
                }
            }
        }
        if (sql.length() == 0 && !valueString.isEmpty())
        {
            value = exchange(valueString.trim());
            result.data.add(value);
        }
        result.type = OrderType.INSERT;
        result.tableNames.add(sqlS[2]);
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
    static ParseResult parseUPDATE(String sql)
    {
//        sqlString = sqlString.replaceAll(";", " ;");
        String sqlS[] =  sql.split(" ");
        ParseResult result = new ParseResult();
        result.tableNames.add(sqlS[1]);
        int status = 0;
//        Value value = new Value();
        SetValue value = new SetValue();
        String right = "";
        String left = "";
        boolean startCopy = false;
        String conds = "";
        boolean isReading = false;
        sql = sql.substring(sql.toUpperCase().indexOf("SET")+3);
        while(sql.length() > 0)
        {
            if (status == 0)
            {
                if (sql.charAt(0) == '=')
                {
                    left = left.trim();
                    status = 1;
                    sql = sql.substring(1);
                }
                else
                {
                    left += sql.charAt(0);
                    sql = sql.substring(1);
                }
            }
            else if (status == 1)
            {
                if (sql.charAt(0) == '\'')
                {
                    isReading = !isReading;
                }
                if (!isReading && (sql.charAt(0) == ',' || sql.toUpperCase().startsWith("WHERE")))
                {
                    Object r = exchange(right.trim());
                    right = "";
                    if (r instanceof SetValue)
                    {
                        value = (SetValue)r;
                    }
                    else
                    {
                        value.isVar1 = false;
                        value.value1 = r;
                    }
                    value.columnName = left;
                    left = "";
                    result.values.add(value);
                    value = new SetValue();
                    if (sql.toUpperCase().startsWith("WHERE"))
                    {
                        break;
                    }
                    else
                    {
                        sql = sql.substring(1);
                        status = 0;
                    }
                }
                else
                {
                    right += sql.charAt(0);
                    sql = sql.substring(1);
                }
            }
        }
        result.type = OrderType.UPDATE;
        result.where = ConditionParser.parseCondition(sql, result.tableNames.get(0));
        return result;
    }
    static ParseResult parseSELECT(String sql)
    {
        sql = sql.trim();
        sql = sql.substring(6);
        int status = 0;
        ParseResult result = new ParseResult();
        String rowName = "";
        String tableName = "";
        String lastTableName = "";
        boolean needToUpdate = false;
        while (sql.length() > 0)
        {
            if (status == 0)
            {
                if (sql.charAt(0) == ',' || sql.toUpperCase().startsWith("FROM"))
                {
                    rowName = rowName.trim();
                    if (rowName.contains("("))
                    {
                        if (rowName.startsWith("SUM"))
                        {
                            result.func = Func.SUM;
                        }
                        else if (rowName.startsWith("AVG"))
                        {
                            result.func = Func.AVG;
                        }
                        else if (rowName.startsWith("COUNT"))
                        {
                            result.func = Func.COUNT;
                        }
                        else if (rowName.startsWith("MIN"))
                        {
                            result.func = Func.MIN;
                        }
                        else
                        {
                            result.func = Func.MAX;
                        }
                        rowName = rowName.substring(rowName.indexOf("(")+1, rowName.indexOf(")")).trim();
                    }
                    if (rowName.contains("."))
                    {
                        result.selectOption.add(rowName.substring(0, rowName.indexOf(".")), rowName.substring(rowName.indexOf(".")+1));
                    }
                    else
                    {
                        if (rowName.equals("*"))
                        {
                            result.selectOption.tableNames = null;
                            result.selectOption.columnNames = null;
                        }
                        else
                        {
                            result.selectOption.columnNames.add(rowName);
                            needToUpdate = true;
                        }
                    }
                    rowName = "";
                    if (sql.toUpperCase().startsWith("FROM"))
                    {
                        sql = sql.substring(4);
                        status = 1;
                    }
                    else
                    {
                        sql = sql.substring(1);
                    }
                }
                else
                {
                    rowName += sql.charAt(0);
                    sql = sql.substring(1);
                }
            }
            else
            {
                if (sql.charAt(0) == ',' || sql.charAt(0) == ';' || sql.toUpperCase().startsWith("WHERE"))
                {
                    result.selectOption.addFromTable(tableName.trim());
                    lastTableName = tableName.trim();
                    tableName = "";
                    if (sql.charAt(0) == ',' || sql.charAt(0) == ';')
                    {
                        sql = sql.substring(1);
                    }
                    else
                    {
                        sql = sql.substring(5);
                        break;
                    }
                }
                else
                {
                    tableName += sql.charAt(0);
                    sql = sql.substring(1);
                }
            }

        }
        if (needToUpdate)
        {
            for (int i = 0; i < result.selectOption.columnNames.size(); i++)
            {
                result.selectOption.tableNames.add(lastTableName);
            }
        }
        result.type = OrderType.SELECT;
        if (!sql.isEmpty())
        {
            if (!result.selectOption.tableNames.isEmpty())
            {
                result.where = ConditionParser.parseCondition(sql, result.selectOption.tableNames.get(0));
            }
            else
            {
                result.where = ConditionParser.parseCondition(sql, lastTableName);
            }
        }
        else
        {
            result.where = new Where(true);
        }
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
    static ParseResult parseCREATE_TABLE(String sql)
    {
        sql = sql.replaceAll(";", " ;");
        sql = sql.replaceAll("\\( ", "(");
        sql = sql.replaceAll(" \\(", "(");
        sql = sql.replaceAll("\\(", " ( ");
        sql = sql.replaceAll("\\) ", ")");
        sql = sql.replaceAll(" \\)", ")");
        sql = sql.replaceAll("\\)", " ) ");
        String sqlS[] =  sql.split(" ");
        ParseResult result = new ParseResult();
//		result.tableNames.add(sqlS[2]);
        sql = "";
        for (int i = 2; i < sqlS.length; i++)
        {
            sql += sqlS[i] + ' ';
        }
//        System.out.println(sql);
//		return null;
        int status = 0;
        while(sql.length() > 0)
        {
            if (status == 0)
            {
                sql = sql.trim();
                DoubleReturn<String, String> r = readString(sql);
                result.tableNames.add(r.first);
                sql = r.second;
                sql = sql.trim();
                sql = sql.substring(1);
                status = 1;
            }
            else if (status == 1)
            {
                if (sql.startsWith(";"))
                {
                    break;
                }
                else if (!sql.toUpperCase().startsWith("PRIMARY KEY"))
                {
                    DoubleReturn<Column, String> r = readColumn(sql);
                    result.columns.add(r.first);
                    sql = r.second;
                }
                else
                {
                    sql = sql.substring(11);
                    sql = sql.trim();
                    sql = sql.substring(1);
                    sql = sql.trim();
                    sql = sql.trim();
                    DoubleReturn<String, String> r = readString(sql);
                    for (int i = 0; i < result.columns.size(); i++)
                    {
                        if (result.columns.get(i).name.equals(r.first))
                        {
                            result.columns.get(i).setPrimary();
                            break;
                        }
                    }
                    break;
                }
            }
        }
        result.type = OrderType.CREATE_TABLE;
        return result;
    }
    public static void main(String[] args)
    {
//		Scanner s = new Scanner(System.in);
//		String sql = s.nextLine();
//        System.out.println(calcStm("10000"));
        ParseResult result = parse("SELECT MAX(title) FROM table;");
//		System.out.print("123");
    }
    static String preParse(String sql)
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
        return sql;
    }
    public static ParseResult parse(String sql)
    {
        sql = sql.trim();
        String preSql = preParse(sql);
        String ss[] =  preSql.split(" ");
        ParseResult result = new ParseResult();
//		try
//		{
        if (ss[0].toUpperCase().equals("INSERT"))
        {
            result = parseINSERT(sql);
        }
        else if (ss[0].toUpperCase().equals("DELETE"))
        {
            result = parseDELETE(preSql);
        }
        else if (ss[0].toUpperCase().equals("UPDATE"))
        {
            result = parseUPDATE(preSql);
        }
        else if (ss[0].toUpperCase().equals("SELECT"))
        {
            result = parseSELECT(preSql);
        }
        else if (ss[0].toUpperCase().equals("CREATE") && ss[1].toUpperCase().equals("DATABASE"))
        {
            result = parseCREATE_DATABASE(preSql);
        }
        else if (ss[0].toUpperCase().equals("DROP") && ss[1].toUpperCase().equals("DATABASE"))
        {
            result = parseDROP_DATABASE(preSql);
        }
        else if (ss[0].toUpperCase().equals("USE"))
        {
            result = parseUSE(preSql);
        }
        else if (ss[0].toUpperCase().equals("SHOW") && ss[1].toUpperCase().equals("TABLES;"))
        {
            result = parseSHOW_TABLES(preSql);
        }
        else if (ss[0].toUpperCase().equals("CREATE") && ss[1].toUpperCase().equals("TABLE"))
        {
            result = parseCREATE_TABLE(preSql);
        }
        else if (ss[0].toUpperCase().equals("DROP") && ss[1].toUpperCase().equals("TABLE"))
        {
            result = parseDROP_TABLE(preSql);
        }
        else if (ss[0].toUpperCase().equals("DESC"))
        {
            result = parseDESC(preSql);
        }
        else
        {
            result.type = OrderType.ERROR;
        }
//		} catch (Exception e)
//		{
//			result.type = OrderType.ERROR;
//		}
        return result;
    }
}

