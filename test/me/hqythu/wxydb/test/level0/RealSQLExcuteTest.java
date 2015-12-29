package me.hqythu.wxydb.test.level0;

import me.hqythu.wxydb.WXYDB;
import me.hqythu.wxydb.exception.level0.SQLExecException;
import me.hqythu.wxydb.exception.level1.SQLRecordException;
import me.hqythu.wxydb.manager.RecordManager;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by apple on 15/12/28.
 */
public class RealSQLExcuteTest {

    public static final String TEST_DB = "orderDB";
    public static final String CREATE_FILE = "sql/create.sql";
    public static final String BOOK_FILE = "sql/book.sql";
    public static final String ORDERS_FILE = "sql/orders.sql";
    public static final String PUBLISHER_FILE = "sql/publisher.sql";
    public static final String CUSTOMER_FILE = "sql/customer.sql";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    WXYDB wxydb;

    @Before
    public void setUp() throws Exception {
        wxydb = new WXYDB();
        wxydb.excuteFile(CREATE_FILE);
        wxydb.writeBack();
    }

    @After
    public void tearDown() throws Exception {
        ParseResult parseResult;
        String result;
//        SystemManager.getInstance().close();
        parseResult = SQLParser.parse("drop database orderDB");
        result = parseResult.execute();
    }

    // 重复primary key
    @Test
    public void testInsertDuplicatePrimary() throws Exception {
        ParseResult parseResult;
        List<String> results;
        String result;
        parseResult = SQLParser.parse("INSERT INTO customer VALUES (300001, ‘CHAD CABELLO’, ‘F’);");
        result = parseResult.execute();
        Assert.assertEquals("insert success", result);

        parseResult = SQLParser.parse("INSERT INTO customer VALUES(300002, 'FAUSTO VANNORMAN','F');");
        result = parseResult.execute();
        Assert.assertEquals("insert success", result);

        thrown.expect(SQLExecException.class);
        thrown.expectMessage("can not insert duplicate primary key");
        parseResult = SQLParser.parse("INSERT INTO customer VALUES(300001,'JO CANNADY','M');");
        parseResult.execute();
    }

    // 插入数据类型有误
    @Test
    public void testInsertErrorType() throws Exception {
        ParseResult parseResult;
        thrown.expect(SQLExecException.class);
        thrown.expectMessage("insert error type at 2 column");
        parseResult = SQLParser.parse("INSERT INTO orders VALUES (315000,200001,’eight’);");
        parseResult.execute();
    }

    // 删除数据
    @Test
    public void testDelete() throws Exception {
        ParseResult parseResult;
        List<String> results;
        String result;
        Table table;

        RecordManager.getInstance().setFast();
        results = wxydb.excuteFile(PUBLISHER_FILE);
        for (int i = 0; i < results.size(); i++) {
            Assert.assertEquals("insert success", results.get(i));
        }
        RecordManager.getInstance().clearFast();
        table = SystemManager.getInstance().getTable("publisher");
        Assert.assertEquals(5000,table.getRecordSize());

        parseResult = SQLParser.parse("DELETE FROM publisher WHERE state=’CA’;");
        Assert.assertEquals(ParseResult.OrderType.DELETE,parseResult.type);
        parseResult.execute();
        table = SystemManager.getInstance().getTable("publisher");
        Assert.assertEquals(2474,table.getRecordSize());
    }

    @Test
    public void testUpdate() throws Exception {
        ParseResult parseResult;
        List<String> results;
        List<Object> records;
        String result;
        Table table;

        // 初始化数据数据
//        RecordManager.getInstance().setFast();
//        results = wxydb.excuteFile(BOOK_FILE);
//        for (String temp : results) {
//            Assert.assertEquals("insert success", temp);
//        }
//        RecordManager.getInstance().clearFast();

//        parseResult = SQLParser.parse("UPDATE book SET title=’Nine Times Nine’ WHERE authors=’Anthony Boucher’;");
//        System.out.println(parseResult.tableNames.get(0));
//        System.out.println(parseResult.where);
//        result = parseResult.execute();
//        Assert.assertEquals(result,"update success");
//        table = SystemManager.getInstance().getTable("book");
//        System.out.println(table.getRecordSize());

//        parseResult = SQLParser.parse("select * from book where title=’Nine Times Nine’; ");
//        Assert.assertEquals(ParseResult.OrderType.SELECT,parseResult.type);
//        System.out.println(parseResult.where);
//        System.out.println(parseResult.selectOption.fromTableNames);
////        Assert.assertEquals(1,parseResult.selectOption.fromTableNames.size());
//        result = parseResult.execute();
//        System.out.println(result);
    }

    //    @Test
    public void testSelect() throws Exception {
        ParseResult parseResult;
        List<String> results;
        List<Object> records;
        String result;
        Table table;

        // 初始化数据数据
        results = wxydb.excuteFile(PUBLISHER_FILE);
        for (String temp : results) {
            Assert.assertEquals("insert success", temp);
        }
        results = wxydb.excuteFile(BOOK_FILE);
        for (String temp : results) {
            Assert.assertEquals("insert success", temp);
        }
        results = wxydb.excuteFile(ORDERS_FILE);
        for (String temp : results) {
            Assert.assertEquals("insert success", temp);
        }

        // 列出所有加州出版商的信息
        parseResult = SQLParser.parse("SELECT * FROM publisher WHERE nation=’CA’;");
        parseResult.execute();

        // 列出 authors 字段为空的记录的书名
        parseResult = SQLParser.parse("SELECT title FROM book WHERE authors is null;");
        parseResult.execute();

        // 列出 authors 字段为空的记录的书名
        parseResult = SQLParser.parse("SELECT book.title,orders.quantity FROM book,orders WHERE book.id=orders.book_id AND orders.quantity>8;");
        parseResult.execute();
    }
}
