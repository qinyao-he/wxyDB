package me.hqythu.sql;

import me.hqythu.manager.SystemManager;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/24.
 */
public class SQLParserTest {

    public static final String TEST_DB = "hello";

    @Test
    public void testCreateDataBase() {
        ParseResult sql;

        // create DataBase
        sql = SQLParser.parse("CREATE DATABASE "+TEST_DB+";");
        Assert.assertTrue(sql.type == ParseResult.OrderType.CREATE_DATABASE);
        Assert.assertTrue(sql.dataBaseName.equals(TEST_DB));
        Assert.assertTrue(SystemManager.getInstance().createDatabase(sql.dataBaseName));

        // drop DataBase
        sql = SQLParser.parse("DROP DATABASE "+TEST_DB+";");
        Assert.assertTrue(sql.type == ParseResult.OrderType.DROP_DATABASE);
        Assert.assertTrue(sql.dataBaseName.equals(TEST_DB));
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(sql.dataBaseName));
    }

    @Test
    public void testDropDataBase() {

    }
}
