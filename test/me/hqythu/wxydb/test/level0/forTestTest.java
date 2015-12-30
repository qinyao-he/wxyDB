package me.hqythu.wxydb.test.level0;

import me.hqythu.wxydb.WXYDB;
import me.hqythu.wxydb.exception.level0.SQLExecException;
import me.hqythu.wxydb.manager.QueryEngine;
import me.hqythu.wxydb.manager.RecordManager;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import me.hqythu.wxydb.util.SetValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

/**
 * Created by apple on 15/12/30.
 */
public class forTestTest {
    public static final String CREATE_FILE = "sql/create.sql";
    public static final String CREATE_S_FILE = "sql/testJoin.sql";
    public static final String BOOK_FILE = "sql/book.sql";
    public static final String ORDERS_FILE = "sql/orders.sql";
    public static final String PUBLISHER_FILE = "sql/publisher.sql";
    public static final String CUSTOMER_FILE = "sql/customer.sql";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    WXYDB wxydb;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void initAll() throws Exception {
        SystemManager.getInstance().dropDatabase("orderDB");

        wxydb = new WXYDB();
        wxydb.excuteFile(CREATE_FILE);
        wxydb.writeBack();

        RecordManager.getInstance().setFast();
        wxydb.excuteFile(PUBLISHER_FILE);
        wxydb.writeBack();
        wxydb.excuteFile(BOOK_FILE);
        wxydb.writeBack();
        wxydb.excuteFile(ORDERS_FILE);
        wxydb.writeBack();
        wxydb.excuteFile(CUSTOMER_FILE);
        wxydb.writeBack();

        SystemManager.getInstance().close();
    }

    @Test
    public void initSmall() throws Exception {
        SystemManager.getInstance().dropDatabase("orderDBsmall");

        wxydb = new WXYDB();
        RecordManager.getInstance().setFast();
        wxydb.excuteFile(CREATE_S_FILE);
        SystemManager.getInstance().close();
        wxydb.writeBack();


    }

    // 栈式的where处理
    // 用大数据集
    @Test
    public void testWhere() throws Exception {
        String sql;
        List<String> results;
        String result;
        List<Object[]> records;
        ParseResult parseResult;

        parseResult = SQLParser.parse("use orderDB");
        parseResult.execute();

        sql = "select * from orders where customer_id > 306000 and (book_id > 220000 or quantity >= 10);";
        parseResult = SQLParser.parse(sql);
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));
    }

    // 聚集查询
    // 用大数据集
    @Test
    public void testFunc() throws Exception {
        String sql;
        List<String> results;
        String result;
        List<Object[]> records;
        ParseResult parseResult;

        parseResult = SQLParser.parse("use orderDB");
        parseResult.execute();

        sql = "select SUM(quantity) from orders where book_id > 220000;";
        parseResult = SQLParser.parse(sql);
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));

        sql = "select AVG(quantity) from orders where book_id > 220000;";
        parseResult = SQLParser.parse(sql);
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));

        sql = "select COUNT(id) from book where id > 220000;";
        parseResult = SQLParser.parse(sql);
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));

        sql = "select MIN(copies) from book;";
        parseResult = SQLParser.parse(sql);
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));

        sql = "select MAX(copies) from book;";
        parseResult = SQLParser.parse(sql);
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));
    }

    // 插入primary key 冲突
    @Test
    public void testInsert() throws Exception {
        String sql;
        List<String> results;
        String result;
        List<Object[]> records;
        ParseResult parseResult;

        parseResult = SQLParser.parse("use orderDB");
        parseResult.execute();

//        thrown.expect(SQLExecException.class);
//        thrown.expectMessage("insert error type at column 3");
//        parseResult = SQLParser.parse("INSERT INTO orders VALUES (315000,200001,'eight');");
//        parseResult.execute();

        thrown.expect(SQLExecException.class);
        thrown.expectMessage("can not insert duplicate primary key");
        parseResult = SQLParser.parse("INSERT INTO customer VALUES(300001,'JO CANNADY','M');");
        result = parseResult.execute();
    }

    // 查询null的处理
    @Test
    public void testSelectNull() throws Exception {
        String sql;
        List<String> results;
        String result;
        List<Object[]> records;
        ParseResult parseResult;

        parseResult = SQLParser.parse("use orderDB");
        parseResult.execute();

        parseResult = SQLParser.parse("SELECT title FROM book WHERE authors is null;");
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));
    }

    // 聚集查询
    // 用小数据集
    @Test
    public void testJoin() throws Exception {
        String sql;
        List<String> results;
        String result;
        List<Object[]> records;
        ParseResult parseResult;

        parseResult = SQLParser.parse("use orderDBsmall");
        parseResult.execute();


        parseResult = SQLParser.parse("SELECT book.title,orders.quantity FROM book,orders WHERE book.id=orders.book_id AND orders.quantity>8;");
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));

        parseResult = SQLParser.parse("SELECT customer.name,book.title,orders.quantity FROM customer,book,orders WHERE orders.customer_id=customer.id AND orders.book_id=book.id AND orders.quantity > 9;");
        records = parseResult.query();
        System.out.println(QueryEngine.resultsToString(records));
    }

}
