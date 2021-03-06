package me.hqythu.wxydb.test.level0;

import me.hqythu.wxydb.WXYDB;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/28.
 */
public class WXYDBTest {

    public static final String CREATE_FILE = "res/sql/create.sql";
    public static final String BOOK_FILE = "res/sql/book.sql";
    public static final String ORDERS_FILE = "res/sql/orders.sql";
    public static final String PUBLISHER_FILE = "res/sql/publisher.sql";
    public static final String CUSTOMER_FILE = "res/sql/customer.sql";

    WXYDB wxydb;

    @Before
    public void setUp() throws Exception {
        wxydb = new WXYDB();
        List<String> results = wxydb.excuteFile(CREATE_FILE);
        wxydb.writeBack();
    }

    @After
    public void tearDown() throws Exception {
        wxydb.writeBack();
    }

    @Test
    public void testExcute() throws Exception {
        ParseResult parseResult;
        List<String> results;
        String result;
//        results = wxydb.excuteFile(ORDERS_FILE);
//        results = wxydb.excuteFile(PUBLISHER_FILE);
//        results = wxydb.excuteFile(CUSTOMER_FILE);
//        parseResult = SQLParser.parse("INSERT INTO publisher VALUES(100008,'Oxbow Books Limited','CA');");
//        result = parseResult.execute();
    }

//    @Test
//    public void testExcuteFile() throws Exception {
//
//    }
}
