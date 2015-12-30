package me.hqythu.wxydb.test.level1;

import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import me.hqythu.wxydb.util.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by apple on 15/12/24.
 */
public class SQLParserTest {

    public static final String TEST_DB = "hello";
    public static final String TEST_TABLE = "Student";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    // Create DB, Drop DB, Use DB
    @Test
    public void testDataBase() {
        ParseResult sql;

        // create DataBase
        sql = SQLParser.parse("CREATE DATABASE orderDB;");
        Assert.assertEquals(ParseResult.OrderType.CREATE_DATABASE, sql.type);
        Assert.assertEquals("orderDB", sql.dataBaseName);

        // use DataBase
        sql = SQLParser.parse("USE orderDB");
        Assert.assertEquals(ParseResult.OrderType.USE, sql.type);
        Assert.assertEquals("orderDB", sql.dataBaseName);

        // drop DataBase
        sql = SQLParser.parse("DROP DATABASE "+TEST_DB+";");
        Assert.assertTrue(sql.type == ParseResult.OrderType.DROP_DATABASE);
        Assert.assertEquals(TEST_DB,sql.dataBaseName);
    }

    // Create Table, Drop Table, Show Tables, DESC Column
    @Test
    public void testTable() {
        ParseResult parseResult;

        parseResult = SQLParser.parse("DROP TABLE customer;");
        Assert.assertEquals(ParseResult.OrderType.DROP_TABLE, parseResult.type);
        Assert.assertEquals("customer", parseResult.tableNames.get(0));

        parseResult = SQLParser.parse("SHOW TABLES;");
        Assert.assertEquals(ParseResult.OrderType.SHOW_TABLES, parseResult.type);

        parseResult = SQLParser.parse("DESC customer;");
        Assert.assertEquals(ParseResult.OrderType.DESC, parseResult.type);
        Assert.assertEquals("customer", parseResult.tableNames.get(0));

        parseResult = SQLParser.parse("CREATE TABLE customer ( id int (10) ) ");
        Assert.assertEquals(ParseResult.OrderType.CREATE_TABLE,parseResult.type);

        parseResult = SQLParser.parse("CREATE TABLE customer ( id int(10) NOT NULL ," +
                "name varchar(25) NOT NULL , gender varchar(1) NOT NULL , PRIMARY KEY(id)" +
                ");");
        Assert.assertEquals("customer",parseResult.tableNames.get(0));
        Assert.assertEquals(ParseResult.OrderType.CREATE_TABLE,parseResult.type);

        parseResult = SQLParser.parse("CREATE TABLE publisher (  id int(10) NOT NULL,  name varchar(100) NOT NULL,  state varchar(2),  PRIMARY KEY  (id));");
        Assert.assertEquals("publisher",parseResult.tableNames.get(0));
        Assert.assertEquals(ParseResult.OrderType.CREATE_TABLE,parseResult.type);

        parseResult = SQLParser.parse("CREATE TABLE orders (" +
                "  customer_id int(10) NOT NULL," +
                "  book_id int(10) NOT NULL," +
                "  quantity int(10) NOT NULL" +
                ");");
        Assert.assertEquals(3,parseResult.columns.size());

    }

    // INSERT
    @Test
    public void testInsert() {
        ParseResult sql;

        // 简单的INSERT: int,varchar,varchar
        sql = SQLParser.parse("INSERT INTO customer VALUES (300001, ‘CHAD CABELLO’, ‘F’);");
        Assert.assertTrue(sql.type == ParseResult.OrderType.INSERT);
        Assert.assertEquals("customer", sql.tableNames.get(0));
        Assert.assertEquals(300001, sql.data.get(0));
        Assert.assertEquals("CHAD CABELLO", sql.data.get(1));
        Assert.assertEquals("F", sql.data.get(2));

        // 简单的INSERT: int,int,varchar
        sql = SQLParser.parse("INSERT INTO orders VALUES (315000,200001,’eight’);");
        Assert.assertTrue(sql.type == ParseResult.OrderType.INSERT);
        Assert.assertEquals("orders", sql.tableNames.get(0));
        Assert.assertEquals(315000, sql.data.get(0));
        Assert.assertEquals(200001, sql.data.get(1));
        Assert.assertEquals("eight", sql.data.get(2));

//        // VALUES紧连着(
        sql = SQLParser.parse("INSERT INTO publisher VALUES(100008,'Oxbow Books Limited','CA');");
        Assert.assertTrue(sql.type == ParseResult.OrderType.INSERT);
        Assert.assertEquals("publisher", sql.tableNames.get(0));
        Assert.assertEquals(3,sql.data.size());
        Assert.assertEquals(100008, sql.data.get(0));
        Assert.assertEquals("Oxbow Books Limited", sql.data.get(1));
        Assert.assertEquals("CA", sql.data.get(2));

//        // 字符串常量含有 ( )
        sql = SQLParser.parse("INSERT INTO book VALUES (200001,'Marias Diary (Plus S.)','Mark P. O. Morford',100082,5991,2530);");
        Assert.assertTrue(sql.type == ParseResult.OrderType.INSERT);
        Assert.assertEquals(6,sql.data.size());
        Assert.assertEquals("book", sql.tableNames.get(0));
        Assert.assertEquals(200001, sql.data.get(0));
        Assert.assertEquals("Marias Diary (Plus S.)", sql.data.get(1));
        Assert.assertEquals("Mark P. O. Morford", sql.data.get(2));
        Assert.assertEquals(100082, sql.data.get(3));
        Assert.assertEquals(5991, sql.data.get(4));
        Assert.assertEquals(2530, sql.data.get(5));

        sql = SQLParser.parse("INSERT INTO publisher VALUES(100050,'Carlton Books, Ltd.','CA');");
        Assert.assertEquals("publisher", sql.tableNames.get(0));
        Assert.assertEquals(3,sql.data.size());
        Assert.assertEquals(100050, sql.data.get(0));
        Assert.assertEquals("Carlton Books, Ltd.", sql.data.get(1));
        Assert.assertEquals("CA", sql.data.get(2));

        sql = SQLParser.parse("INSERT INTO book VALUES(200100,'Anyone Can Have a Happy',null,103343,3358,2213);");
        Assert.assertTrue(sql.type == ParseResult.OrderType.INSERT);
        Assert.assertEquals("book", sql.tableNames.get(0));
        Assert.assertEquals(6,sql.data.size());
        Assert.assertEquals(200100, sql.data.get(0));
        Assert.assertEquals("Anyone Can Have a Happy", sql.data.get(1));
        Assert.assertEquals(null, sql.data.get(2));
        Assert.assertEquals(103343, sql.data.get(3));
        Assert.assertEquals(3358, sql.data.get(4));
        Assert.assertEquals(2213, sql.data.get(5));

    }

    // DELETE
    @Test
    public void testDelete() {
        ParseResult sql;
        sql = SQLParser.parse("DELETE FROM publisher WHERE state is null;");
        Assert.assertTrue(sql.type == ParseResult.OrderType.DELETE);
        Assert.assertEquals("publisher", sql.tableNames.get(0));
        Assert.assertEquals(1, sql.where.boolExprsAndOps.size());
        Assert.assertEquals(1, sql.where.isExprs.size());
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(0) instanceof BoolExpr);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).tableNameL);
        Assert.assertEquals("state", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).columnNameL);
        Assert.assertEquals(CompareOp.IS, ((BoolExpr)sql.where.boolExprsAndOps.get(0)).compareOp);
        Assert.assertEquals(null, ((BoolExpr)sql.where.boolExprsAndOps.get(0)).valueR);

        sql = SQLParser.parse("DELETE FROM publisher WHERE state=’(CA,)’;");
        Assert.assertTrue(sql.type == ParseResult.OrderType.DELETE);
        Assert.assertEquals("publisher", sql.tableNames.get(0));
        Assert.assertEquals(1, sql.where.boolExprsAndOps.size());
        Assert.assertEquals(1, sql.where.isExprs.size());
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(0) instanceof BoolExpr);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).tableNameL);
        Assert.assertEquals("state", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).columnNameL);
        Assert.assertEquals("(CA,)", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).valueR);

        sql = SQLParser.parse("DELETE FROM publisher WHERE a = 1 and (b <> '123' or c < 234 or d is null);");
        Assert.assertTrue(sql.type == ParseResult.OrderType.DELETE);
        Assert.assertEquals("publisher", sql.tableNames.get(0));
        Assert.assertEquals(7, sql.where.boolExprsAndOps.size());
        Assert.assertEquals(7, sql.where.isExprs.size());
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(0) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(1) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(2) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(3) instanceof BoolOp);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(4) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(5) instanceof BoolOp);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(6) instanceof BoolOp);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).tableNameL);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(1)).tableNameL);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(2)).tableNameL);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(4)).tableNameL);
        Assert.assertEquals("a", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).columnNameL);
        Assert.assertEquals("b", ((BoolExpr)sql.where.boolExprsAndOps.get(1)).columnNameL);
        Assert.assertEquals("c", ((BoolExpr)sql.where.boolExprsAndOps.get(2)).columnNameL);
        Assert.assertEquals("d", ((BoolExpr)sql.where.boolExprsAndOps.get(4)).columnNameL);
        Assert.assertEquals(BoolOp.OR, sql.where.boolExprsAndOps.get(3));
        Assert.assertEquals(BoolOp.OR, sql.where.boolExprsAndOps.get(5));
        Assert.assertEquals(BoolOp.AND, sql.where.boolExprsAndOps.get(6));
        Assert.assertEquals(1, ((BoolExpr)sql.where.boolExprsAndOps.get(0)).valueR);
        Assert.assertEquals("123", ((BoolExpr)sql.where.boolExprsAndOps.get(1)).valueR);
        Assert.assertEquals(234, ((BoolExpr)sql.where.boolExprsAndOps.get(2)).valueR);
        Assert.assertEquals(null, ((BoolExpr)sql.where.boolExprsAndOps.get(4)).valueR);
        Assert.assertEquals(CompareOp.IS, ((BoolExpr)sql.where.boolExprsAndOps.get(4)).compareOp);


        sql = SQLParser.parse("DELETE FROM publisher WHERE a=1 or(b<>'123' or(c<234 or d=3));");
        Assert.assertTrue(sql.type == ParseResult.OrderType.DELETE);
        Assert.assertEquals("publisher", sql.tableNames.get(0));
        Assert.assertEquals(7, sql.where.boolExprsAndOps.size());
        Assert.assertEquals(7, sql.where.isExprs.size());
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(0) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(1) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(2) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(3) instanceof BoolExpr);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(4) instanceof BoolOp);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(5) instanceof BoolOp);
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(6) instanceof BoolOp);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).tableNameL);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(1)).tableNameL);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(2)).tableNameL);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(3)).tableNameL);
        Assert.assertEquals("a", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).columnNameL);
        Assert.assertEquals("b", ((BoolExpr)sql.where.boolExprsAndOps.get(1)).columnNameL);
        Assert.assertEquals("c", ((BoolExpr)sql.where.boolExprsAndOps.get(2)).columnNameL);
        Assert.assertEquals("d", ((BoolExpr)sql.where.boolExprsAndOps.get(3)).columnNameL);
        Assert.assertEquals(BoolOp.OR, sql.where.boolExprsAndOps.get(4));
        Assert.assertEquals(BoolOp.OR, sql.where.boolExprsAndOps.get(5));
        Assert.assertEquals(BoolOp.OR, sql.where.boolExprsAndOps.get(6));
        Assert.assertEquals(1, ((BoolExpr)sql.where.boolExprsAndOps.get(0)).valueR);
        Assert.assertEquals("123", ((BoolExpr)sql.where.boolExprsAndOps.get(1)).valueR);
        Assert.assertEquals(234, ((BoolExpr)sql.where.boolExprsAndOps.get(2)).valueR);
        Assert.assertEquals(3, ((BoolExpr)sql.where.boolExprsAndOps.get(3)).valueR);
    }

    // UPDATE
    @Test
    public void testUpdate() {
        ParseResult parseResult;
        parseResult = SQLParser.parse("UPDATE book SET title='Nine Times Nine' WHERE authors='Anthony Boucher';");
        Assert.assertEquals("book",parseResult.tableNames.get(0).toString());
        Assert.assertEquals("set title = Nine Times Nine",SetValue.toString(parseResult.values));
        Assert.assertEquals("[book.authors == Anthony Boucher]",parseResult.where.toString());

        parseResult = SQLParser.parse("UPDATE book SET title='(Nine Times, N213ine)' WHERE authors='Anthony Boucher';");
        Assert.assertTrue(parseResult.tableNames.get(0).equals("book"));
        Assert.assertEquals("set title = (Nine Times, N213ine)",SetValue.toString(parseResult.values));
        Assert.assertEquals("[book.authors == Anthony Boucher]",parseResult.where.toString());

        parseResult = SQLParser.parse("UPDATE book SET title=title+1,b=2*b, c=8-(1+2+3)*4/6+5-7 WHERE authors='Anthony Boucher';");
        Assert.assertTrue(parseResult.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.values.get(0).isVar1);
        Assert.assertTrue(!parseResult.values.get(0).isVar2);
        Assert.assertTrue(parseResult.values.get(0).calcOp.equals(CalcOp.ADD));
        Assert.assertTrue(parseResult.values.get(0).value2.equals(1));
        Assert.assertTrue(!parseResult.values.get(1).isVar1);
        Assert.assertTrue(parseResult.values.get(1).isVar2);
        Assert.assertTrue(parseResult.values.get(1).calcOp.equals(CalcOp.MUL));
        Assert.assertTrue(parseResult.values.get(1).value1.equals(2));
        Assert.assertTrue(!parseResult.values.get(2).isVar1);
        Assert.assertTrue(parseResult.values.get(2).value1.equals(2));


        Assert.assertEquals("[book.authors == Anthony Boucher]",parseResult.where.toString());
    }

    // SELECT
    @Test
    public void testSelect() {
        ParseResult parseResult;
        parseResult = SQLParser.parse("select * from book where title=’Nine Times Nine’;");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));

        parseResult = SQLParser.parse("select a from book where title=’Nine Times Nine’;");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(0).equals("a"));

        parseResult = SQLParser.parse("select book.a,title.b from book,title,BBB where title=’Nine Times Nine’;");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("title"));
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("BBB"));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(1).equals("title"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(0).equals("a"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(1).equals("b"));

        parseResult = SQLParser.parse("select SUM(sum) from book where title=’Nine Times Nine’;");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));
        Assert.assertTrue(parseResult.selectOption.func.equals(Func.SUM));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(0).equals("sum"));

        parseResult = SQLParser.parse("select MAX(sum) from book where title=’Nine Times Nine’;");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));
        Assert.assertTrue(parseResult.selectOption.func.equals(Func.MAX));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(0).equals("sum"));

        parseResult = SQLParser.parse("select COUNT(sum) from book where title=’Nine Times Nine’;");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));
        Assert.assertTrue(parseResult.selectOption.func.equals(Func.COUNT));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(0).equals("sum"));

        parseResult = SQLParser.parse("select AVG(sum) from book where title=’Nine Times Nine’;");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));
        Assert.assertTrue(parseResult.selectOption.func.equals(Func.AVG));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(0).equals("sum"));

        parseResult = SQLParser.parse("select MIN ( sum ) from book where title='Nine Times Nine min';");
        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
        Assert.assertTrue(parseResult.selectOption.fromTableNames.contains("book"));
        Assert.assertTrue(parseResult.selectOption.func.equals(Func.MIN));
        Assert.assertTrue(parseResult.selectOption.tableNames.get(0).equals("book"));
        Assert.assertTrue(parseResult.selectOption.columnNames.get(0).equals("sum"));

        parseResult = SQLParser.parse("select * from orders where (customer_id > 305000 and book_id > 210000) or quantity >= 10;");
        System.out.println(parseResult.where);
    }
}
