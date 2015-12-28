package me.hqythu.wxydb.test.level0;

import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import me.hqythu.wxydb.util.BoolExpr;
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
        ParseResult sql;

        sql = SQLParser.parse("DROP TABLE customer;");
        Assert.assertEquals(ParseResult.OrderType.DROP_TABLE, sql.type);
        Assert.assertEquals("customer", sql.tableNames.get(0));

        sql = SQLParser.parse("SHOW TABLES;");
        Assert.assertEquals(ParseResult.OrderType.SHOW_TABLES, sql.type);

        sql = SQLParser.parse("DESC customer;");
        Assert.assertEquals(ParseResult.OrderType.DESC, sql.type);
        Assert.assertEquals("customer", sql.tableNames.get(0));

        sql = SQLParser.parse("CREATE TABLE customer ( id int (10) ) ");
        Assert.assertEquals(ParseResult.OrderType.CREATE_TABLE,sql.type);

        sql = SQLParser.parse("CREATE TABLE customer ( id int(10) NOT NULL ," +
                "name varchar(25) NOT NULL , gender varchar(1) NOT NULL , PRIMARY KEY(id)" +
                ");");
        Assert.assertEquals("customer",sql.tableNames.get(0));
        Assert.assertEquals(ParseResult.OrderType.CREATE_TABLE,sql.type);

        sql = SQLParser.parse("CREATE TABLE publisher (  id int(10) NOT NULL,  name varchar(100) NOT NULL,  state varchar(2),  PRIMARY KEY  (id));");
        Assert.assertEquals("publisher",sql.tableNames.get(0));
        Assert.assertEquals(ParseResult.OrderType.CREATE_TABLE,sql.type);

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



    }

    // DELETE
    @Test
    public void testDelete() {
        ParseResult sql;
        sql = SQLParser.parse("DELETE FROM publisher WHERE state=’CA’;");
        Assert.assertTrue(sql.type == ParseResult.OrderType.DELETE);
        Assert.assertEquals("publisher", sql.tableNames.get(0));
        Assert.assertEquals(1, sql.where.boolExprsAndOps.size());
        Assert.assertEquals(1, sql.where.isExprs.size());
        Assert.assertEquals(true, sql.where.boolExprsAndOps.get(0) instanceof BoolExpr);
        Assert.assertEquals("publisher", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).tableNameL);
        Assert.assertEquals("state", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).columnNameL);
        Assert.assertEquals("CA", ((BoolExpr)sql.where.boolExprsAndOps.get(0)).valueR);
    }

    // UPDATE
    @Test
    public void testUpdate() {

    }

    // SELECT
    public void testSelect() {

    }
}
