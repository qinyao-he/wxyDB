package me.hqythu.wxydb.test.level0;

import me.hqythu.wxydb.WXYDB;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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
        parseResult = SQLParser.parse("drop database orderDB");
        result = parseResult.execute();
    }

    @Test
    public void testInsertCheck() throws Exception {
        ParseResult parseResult;
        List<String> results;
        String result;

        parseResult = SQLParser.parse("INSERT INTO customer VALUES (300001, ‘CHAD CABELLO’, ‘F’);");
        result = parseResult.execute();
        Assert.assertEquals("insert success",result);

        parseResult = SQLParser.parse("INSERT INTO customer VALUES(300002, 'FAUSTO VANNORMAN','F');");
        result = parseResult.execute();
        Assert.assertEquals("insert success",result);

        parseResult = SQLParser.parse("INSERT INTO customer VALUES(300001,'JO CANNADY','M');");

        result = parseResult.execute();


        parseResult = SQLParser.parse("INSERT INTO orders VALUES (315000,200001,’eight’);");
        result = parseResult.execute();
        System.out.println(result);
    }

//    @Test
    public void testDelete() throws Exception {
        ParseResult parseResult;
        List<String> results;
        String result;
        Table table;

        // 初始化数据数据
        results = wxydb.excuteFile(PUBLISHER_FILE);
        System.out.println(results.size());
        for (String temp : results) {
            Assert.assertEquals(temp,"insert success");
        }
        table = SystemManager.getInstance().getTable("publisher");
        System.out.println(table.getRecordSize());

        parseResult = SQLParser.parse("DELETE FROM publisher WHERE state=’CA’;");
        parseResult.execute();
        table = SystemManager.getInstance().getTable("publisher");
        System.out.println(table.getRecordSize());
    }

//    @Test
    public void testUpdate() throws Exception {
        ParseResult parseResult;
        List<String> results;
        List<Object> records;
        String result;
        Table table;

        // 初始化数据数据
        results = wxydb.excuteFile(BOOK_FILE);
        for (String temp : results) {
            Assert.assertEquals(temp,"insert success");
        }

        parseResult = SQLParser.parse("UPDATE book SET title=’Nine Times Nine’ WHERE authors=’Anthony Boucher’;");
        parseResult.execute();
        table = SystemManager.getInstance().getTable("book");
        System.out.println(table.getRecordSize());

        parseResult = SQLParser.parse("select * from book where title=’Nine Times Nine’ ");
        result = parseResult.execute();

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
            Assert.assertNotEquals(temp,"insert success");
        }
        results = wxydb.excuteFile(BOOK_FILE);
        for (String temp : results) {
            Assert.assertNotEquals(temp,"insert success");
        }
        results = wxydb.excuteFile(ORDERS_FILE);
        for (String temp : results) {
            Assert.assertNotEquals(temp,"insert success");
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
