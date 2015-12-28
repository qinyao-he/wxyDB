package me.hqythu.wxydb.test.level2;

import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Column;
import me.hqythu.wxydb.object.DataType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/27.
 */
public class ColumnTest {
    public static final String TEST_DB = "test.db";
    public static final String TEST_TABLE1 = "Customer";
    @Before
    public void setUp() throws Exception {
        SystemManager.getInstance().createDatabase(TEST_DB);
        SystemManager.getInstance().useDatabase(TEST_DB);
    }

    @After
    public void tearDown() throws Exception {
        SystemManager.getInstance().dropDatabase(TEST_DB);
    }

    @Test
    public void testNewColumn() throws  Exception {
        List<Column> columns = new ArrayList<>();
        // 创建表
        columns.clear();
        columns.add(new Column("id", DataType.INT,(short)4).setPrimary());
        columns.add(new Column("name",DataType.VARCHAR,(short)100).setNotNull());
        columns.add(new Column("sex",DataType.VARCHAR,(short)1));
        Assert.assertTrue(SystemManager.getInstance().createTable("Customer",columns));
    }

}
