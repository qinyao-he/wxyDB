package me.hqythu.wxydb.test.level0;

import me.hqythu.wxydb.WXYDB;
import me.hqythu.wxydb.sql.ParseResult;
import me.hqythu.wxydb.sql.SQLParser;
import org.junit.After;
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
        List<String> results = wxydb.excuteFile(CREATE_FILE);
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
    public void testPoint() throws Exception {
        ParseResult parseResult;
        List<String> results;
        String result;

        parseResult = SQLParser.parse("INSERT INTO customer VALUES (300001, ‘CHAD CABELLO’, ‘F’);");
        result = parseResult.execute();
        System.out.println(result);
        parseResult = SQLParser.parse("INSERT INTO customer VALUES(300002, 'FAUSTO VANNORMAN','F');");
        result = parseResult.execute();
        System.out.println(result);
        parseResult = SQLParser.parse("INSERT INTO customer VALUES(300001,'JO CANNADY','M');");
        result = parseResult.execute();
        System.out.println(result);
    }
}
