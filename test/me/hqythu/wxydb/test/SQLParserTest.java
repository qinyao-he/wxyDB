package me.hqythu.wxydb.test;

import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
    }

    // INSERT
    @Test
    public void testInsert() {
        ParseResult sql;

        sql = SQLParser.parse("INSERT INTO customer VALUES (300001, ‘CHAD CABELLO’, ‘F’);");
        Assert.assertTrue(sql.type == ParseResult.OrderType.INSERT);
        Assert.assertEquals("customer", sql.tableNames.get(0));
        Assert.assertEquals(300001, sql.data.get(0));
        Assert.assertEquals("CHAD CABELLO", sql.data.get(1));
        Assert.assertEquals("F", sql.data.get(2));

        sql = SQLParser.parse("INSERT INTO orders VALUES (315000,200001,’eight’);");
        Assert.assertTrue(sql.type == ParseResult.OrderType.INSERT);
        Assert.assertEquals("orders", sql.tableNames.get(0));
        Assert.assertEquals(315000, sql.data.get(0));
        Assert.assertEquals(200001, sql.data.get(1));
        Assert.assertEquals("eight", sql.data.get(2));

    }

    // DELETE
    @Test
    public void testDelete() {

    }

    // UPDATE
    @Test
    public void testUpdate() {

    }

    // SELECT
    public void testSelect() {

    }
}
