package me.hqythu.manager;

import me.hqythu.object.Column;
import me.hqythu.object.DataType;
import me.hqythu.object.Record;
import me.hqythu.object.Table;
import me.hqythu.pagefile.BufPageManager;
import me.hqythu.pagefile.Page;
import me.hqythu.pagefile.TablePageUser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/24.
 */
public class RecordManagerTest {

    public static final String TEST_DB = "test_record.db";
    public static final String TEST_NEWDB = "test_hello.db";
    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";

    @Before
    public void setUp() throws Exception {
        SystemManager.getInstance().createDatabase(TEST_DB);
        SystemManager.getInstance().useDatabase(TEST_DB);

        // 创建表
        Column cols[] = new Column[2];
        cols[0] = new Column("name", DataType.VARCHAR,(short)40);
        cols[1] = new Column("age",DataType.INT,(short)4);
        Assert.assertTrue(SystemManager.getInstance().createTable(TEST_TABLE1,cols));

        // 创建表
        Column cols2[] = new Column[3];
        cols2[0] = new Column("id",DataType.INT,(short)4);
        cols2[1] = new Column("name",DataType.VARCHAR,(short)100);
        cols2[2] = new Column("sex",DataType.VARCHAR,(short)1);
        Assert.assertTrue(SystemManager.getInstance().createTable(TEST_TABLE2,cols2));
    }

    @After
    public void tearDown() throws Exception {
        SystemManager.getInstance().dropDatabase(TEST_DB);
    }

    /**
     * 测试插入
     * 被测函数如下
     * public void insert(String tableName, Object[] values)
     * public void insert(String tableName, String[] fields, Object[] values)
     * public void insert(String tableName, int[] cols, Object[] values)
     */
    @Test
    public void testInsert() throws Exception {
        Table table;
        int pageId;
        int fileId;
        String fields[];
        int[] cols;

        // 初始化插入数据
        Object[] record = new Object[2];
        record[0] = "LiuXiaoHong";
        record[1] = 18;
        fields = new String[]{"name","age"};
        cols = new int[]{0,1};

        // 3种插入方式
        RecordManager.getInstance().insert(TEST_TABLE1,record);
        RecordManager.getInstance().insert(TEST_TABLE1,fields,record);
        RecordManager.getInstance().insert(TEST_TABLE1,cols,record);

        // 检查记录个数
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        pageId = table.getPageId();
        fileId = SystemManager.getInstance().fileId;
        Assert.assertTrue(pageId != -1);
        Assert.assertTrue(fileId != -1);
        Page tPage = BufPageManager.getInstance().getPage(fileId,pageId);

        Assert.assertSame(3,TablePageUser.getRecordSize(tPage));

        // 切换数据库之后,再检查记录个数
        Assert.assertTrue(SystemManager.getInstance().createDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        pageId = table.getPageId();
        fileId = SystemManager.getInstance().fileId;
        Assert.assertTrue(pageId != -1);
        Assert.assertTrue(fileId != -1);

        Assert.assertSame(3,TablePageUser.getRecordSize(tPage));

        

    }

//    @Test
//    public void testInsert1() throws Exception {
//
//    }
//
//    @Test
//    public void testInsert2() throws Exception {
//
//    }
//
//    @Test
//    public void testRemove() throws Exception {
//
//    }
//
//    @Test
//    public void testUpdate() throws Exception {
//
//    }
//
//    @Test
//    public void testUpdate1() throws Exception {
//
//    }
}
