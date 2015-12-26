package me.hqythu.wxydb.sql;

import me.hqythu.wxydb.sql.ParseResult.OrderType;

public class SQLParser {
    static Object exchange(String value) {
        Object resultObject;
        value = value.replaceAll("\\,", "");
        value = value.replaceAll("\\)", "");
        value = value.replaceAll("\\;", "");
        if (value.startsWith("\'") || value.startsWith("‘") || value.startsWith("’")) {
            value = value.replaceAll("\\'", "");
            value = value.replaceAll("\\‘", "");
            value = value.replaceAll("\\’", "");
            resultObject = value;
        } else if (value.contains(".")) {
            resultObject = value;
        } else {
            resultObject = Integer.valueOf(value);
        }
        return resultObject;
    }

    static ParseResult parseINSERT(String sqlString) {
        sqlString = sqlString.replaceAll(" \\, ", ", ");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        String currentString = "";
        for (int i = 4; i < sql.length; i++) {
            sql[i] = sql[i].replaceAll("\\(", "");
            sql[i] = sql[i].replaceAll("\\)", "");
            currentString += sql[i];
            if (currentString.endsWith(",") || currentString.endsWith(";")) {
                result.data.add(exchange(currentString));
                currentString = "";
            } else {
                currentString += ' ';
            }
        }
        result.type = OrderType.INSERT;
        result.tableNames.add(sql[2]);
        return result;
    }

    static ParseResult parseDELETE(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        result.tableNames.add(sql[2]);
        String conds = "";
        for (int i = 4; i < sql.length; i++) {
            if (sql[i].isEmpty()) {
                continue;
            }
            conds += sql[i] + ' ';
        }
        result.type = OrderType.DELETE;
        result.conditions = ConditionParser.parseCondition(conds);
        return result;
    }

    static ParseResult parseUPDATE(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        result.tableNames.add(sql[1]);
        int num = 0;
        Value value = new Value();
        String right = "";
        boolean startCopy = false;
        String conds = "";
        for (int i = 3; i < sql.length; i++) {
            if (sql[i].toUpperCase().equals("WHERE")) {
                startCopy = true;
                continue;
            }
            if (startCopy) {
                conds += sql[i] + ' ';
            } else {
                if (num == 0) {
                    value.left = sql[i];
                    i++;
                    num = 1;
                    continue;
                } else if (num == 1) {
                    if (sql[i + 1].toUpperCase().equals("WHERE") || sql[i + 1].equals(",")) {
                        right += sql[i];
                        value.right = exchange(right);
                        result.values.add(value);
                        value = new Value();
                        num = 0;
                        if (sql[i + 1].equals(",")) {
                            i++;
                        }
                        right = "";
                    } else {
                        right += sql[i];
                        right += ' ';
                    }
                }
            }
        }
        result.type = OrderType.UPDATE;
        result.conditions = ConditionParser.parseCondition(conds);
        return result;
    }

    static ParseResult parseSELECT(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        int num = 0;
        String conds = "";
        for (int i = 1; i < sql.length; i++) {
            if (num == 0) {
                if (sql[i].toUpperCase().equals("FROM")) {
                    num = 1;
                    continue;
                } else if (!sql[i].equals(",")) {
                    result.rowNames.add(sql[i]);
                }
            } else if (num == 1) {
                if (sql[i].toUpperCase().equals("WHERE")) {
                    num = 2;
                    continue;
                } else if (!sql[i].equals(",")) {
                    result.tableNames.add(sql[i]);
                }

            } else {
                conds += sql[i] + ' ';
            }
        }
        result.type = OrderType.SELECT;
        result.conditions = ConditionParser.parseCondition(conds);
        return result;
    }

    static ParseResult parseCREATE_DATABASE(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        result.type = OrderType.CREATE_DATABASE;
        result.dataBaseName = sql[2];
        return result;
    }

    static ParseResult parseDROP_DATABASE(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        result.type = OrderType.DROP_DATABASE;
        result.dataBaseName = sql[2];
        return result;
    }

    static ParseResult parseUSE(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        result.type = OrderType.USE;
        result.dataBaseName = sql[1];
        return result;
    }

    static ParseResult parseSHOW_TABLES(String sqlString) {
        ParseResult result = new ParseResult();
        result.type = OrderType.SHOW_TABLES;
        return result;
    }

    static ParseResult parseDROP_TABLE(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        result.type = OrderType.DROP_TABLE;
        result.tableNames.add(sql[2]);
        return result;
    }

    static ParseResult parseDESC(String sqlString) {
        sqlString = sqlString.replaceAll(";", " ;");
        String sql[] = sqlString.split(" ");
        ParseResult result = new ParseResult();
        result.type = OrderType.DESC;
        result.tableNames.add(sql[1]);
        return result;
    }

    //    public static void main(String[] args)
//    {
//        Scanner s = new Scanner(System.in);
//        String sql = s.nextLine();
//        ParseResult result = handleSQL(sql);
//    }
    public static ParseResult parse(String sql) {
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
        String ss[] = sql.split(" ");
        ParseResult result = new ParseResult();
        try {
            if (ss[0].toUpperCase().equals("INSERT")) {
                result = parseINSERT(sql);
            } else if (ss[0].toUpperCase().equals("DELETE")) {
                result = parseDELETE(sql);
            } else if (ss[0].toUpperCase().equals("UPDATE")) {
                result = parseUPDATE(sql);
            } else if (ss[0].toUpperCase().equals("SELECT")) {
                result = parseSELECT(sql);
            } else if (ss[0].toUpperCase().equals("CREATE") && ss[1].toUpperCase().equals("DATABASE")) {
                result = parseCREATE_DATABASE(sql);
            } else if (ss[0].toUpperCase().equals("DROP") && ss[1].toUpperCase().equals("DATABASE")) {
                result = parseDROP_TABLE(sql);
            } else if (ss[0].toUpperCase().equals("USE")) {
                result = parseUSE(sql);
            } else if (ss[0].toUpperCase().equals("SHOW") && ss[1].toUpperCase().equals("TABLES;")) {
                result = parseSHOW_TABLES(sql);
            }
//			else if (ss[0].toUpperCase().equals("CREATE") && ss[1].toUpperCase().equals("TABLE"))
//			{
//				result = parseCREATE_TABLE(sql);
//			}
            else if (ss[0].toUpperCase().equals("DROP") && ss[1].toUpperCase().equals("TABLE")) {
                result = parseDROP_TABLE(sql);
            } else if (ss[0].toUpperCase().equals("DESC")) {
                result = parseDESC(sql);
            } else {
                result.type = OrderType.ERROR;
            }
        } catch (Exception e) {
            result.type = OrderType.ERROR;
        }
        return result;
    }

}
