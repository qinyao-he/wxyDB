package me.hqythu.sql;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/24.
 */
public class SQLParserTest {
    @Test
    public void testCreateDataBase() {
        ParseResult sql = SQLParser.parse("CREATE DATABASE hello;");
        Assert.assertTrue("创建数据库",sql.type == ParseResult.OrderType.CREATE_DATABASE);
        Assert.assertTrue(sql.dataBaseName.equals("hello"));
    }

    @Test
    public void testDropDataBase() {

    }
}
