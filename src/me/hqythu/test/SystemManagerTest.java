package me.hqythu.test;

import me.hqythu.manager.SystemManager;
import me.hqythu.object.Column;
import me.hqythu.object.DataType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 系统模块单元测试
 * test.db为测试文件,测试函数请不要删除test.db
 */

public class SystemManagerTest {
    public static final String TEST_DB = "test.db";
    public static final String TEST_NEWDB = "newdb.db";
    public static final String TEST_OTHERDB = "hello.db";

    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";

    @Before
    public void setUp() throws Exception {
        SystemManager.getInstance().createDatabase(TEST_DB);
    }

    @After
    public void tearDown() throws Exception {
        SystemManager.getInstance().dropDatabase(TEST_DB);
    }

    /**
     * 测试数据库的创建,切换,删除
     * @throws Exception
     */
    @Test
    public void testOpDatabase() throws Exception {

        // 正常创建数据库
        Assert.assertTrue(SystemManager.getInstance().createDatabase(TEST_NEWDB));
        // 无法调用不存在的文件
        Assert.assertFalse(SystemManager.getInstance().useDatabase(TEST_OTHERDB));
        // 正常切换数据库
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        // 允许删除正在使用的数据库
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        // 无法调用已经被删除的数据库
        Assert.assertFalse(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        // 无法删除不存在的数据库
        Assert.assertFalse(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        // 正常切换数据库
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));

    }

    /**
     * 测试表的创建,显示,删除
     * @throws Exception
     */
    @Test
    public void testOpTable() throws Exception {
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));

        // 创建表
        Column cols[] = new Column[2];
        cols[0] = new Column("name",DataType.VARCHAR,(short)20);
        cols[1] = new Column("age",DataType.INT,(short)4);
        Assert.assertTrue(SystemManager.getInstance().createTable("Student",cols));

        // 创建表
        Column cols2[] = new Column[3];
        cols2[0] = new Column("id",DataType.INT,(short)4);
        cols2[1] = new Column("name",DataType.VARCHAR,(short)100);
        cols2[2] = new Column("sex",DataType.VARCHAR,(short)1);
        Assert.assertTrue(SystemManager.getInstance().createTable("Customer",cols2));

        // 无法删除不存在的表
        Assert.assertFalse(SystemManager.getInstance().dropTable("Hello"));
        // 正常删除表
        Assert.assertTrue(SystemManager.getInstance().dropTable("Customer"));
        // 无法删除不存在的表
        Assert.assertFalse(SystemManager.getInstance().dropTable("Customer"));

        // 重新打开数据库,表信息是否仍然正确
        Assert.assertTrue(SystemManager.getInstance().createDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));
    }

    @Test
    public void testShow() {
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));
        String columnInfos;

        // 创建表
        Column cols[] = new Column[2];
        cols[0] = new Column("name",DataType.VARCHAR,(short)20);
        cols[1] = new Column("age",DataType.INT,(short)4);
        Assert.assertTrue(SystemManager.getInstance().createTable(TEST_TABLE1,cols));

        // 创建表
        Column cols2[] = new Column[3];
        cols2[0] = new Column("id",DataType.INT,(short)4);
        cols2[0].setPrimary();
        cols2[1] = new Column("name",DataType.VARCHAR,(short)100);
        cols2[2] = new Column("sex",DataType.VARCHAR,(short)1);
        Assert.assertTrue(SystemManager.getInstance().createTable(TEST_TABLE2,cols2));

        columnInfos = SystemManager.getInstance().showTableColumns(TEST_TABLE1);
        Assert.assertEquals("[name(varchar(20)),age(int)]",columnInfos);
        columnInfos = SystemManager.getInstance().showTableColumns(TEST_TABLE2);
        Assert.assertEquals("[id(int) primary key,name(varchar(100)),sex(varchar(1))]",columnInfos);
    }

}
