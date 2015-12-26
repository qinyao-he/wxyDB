package me.hqythu.test;

import me.hqythu.exception.SQLRecordException;
import me.hqythu.manager.RecordManager;
import me.hqythu.manager.SystemManager;
import me.hqythu.object.Column;
import me.hqythu.object.DataType;
import me.hqythu.object.Record;
import me.hqythu.object.Table;
import me.hqythu.pagefile.BufPageManager;
import me.hqythu.pagefile.DataPageUser;
import me.hqythu.pagefile.Page;
import me.hqythu.pagefile.TablePageUser;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/24.
 */
public class RecordManagerTest {

    public static final String TEST_DB = "test_record.db";
    public static final String TEST_NEWDB = "test_hello.db";
    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";
    public static final int BIG_NUM = 10000;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        SystemManager.getInstance().createDatabase(TEST_DB);
        SystemManager.getInstance().useDatabase(TEST_DB);

        // 创建表
        Column cols[] = new Column[2];
        // Student: name:varchar(40), age:int not null
        cols[0] = new Column("name", DataType.VARCHAR,(short)40);
        cols[1] = new Column("age",DataType.INT,(short)4);
        cols[1].setNotNull();
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
     * 简单的测试插入
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
        Object[] record;
        String fields[];
        int[] cols;
        Page tPage;
        Page dPage;
        byte[] data;
        ByteBuffer buffer;
        String str;

        // 初始化插入数据
        record = new Object[2];
        fields = new String[]{"name","age"};
        cols = new int[]{0,1};

        // 3种插入方式
        record[0] = "LiuXiaoHong";
        record[1] = 18;
        RecordManager.getInstance().insert(TEST_TABLE1,record);
        record[1] = 19;
        RecordManager.getInstance().insert(TEST_TABLE1,fields,record);

        // 检查记录个数
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        pageId = table.getPageId();
        fileId = SystemManager.getInstance().getFileId();
        Assert.assertTrue(pageId != -1);
        Assert.assertTrue(fileId != -1);
        tPage = BufPageManager.getInstance().getPage(fileId,pageId);
        Assert.assertEquals(2,TablePageUser.getRecordSize(tPage));

        // 检查数据页的写入情况
        pageId = TablePageUser.getFirstDataPage(tPage);
        Assert.assertTrue(pageId != -1);
        dPage = BufPageManager.getInstance().getPage(fileId,pageId);
        Assert.assertEquals(2,DataPageUser.getRecordSize(dPage));
        data = DataPageUser.readRecord(dPage,0);
        Assert.assertNotEquals(null,data);
        str = new String(data,6,40);
        if (str.indexOf(0) > 0) {
            str = str.substring(0,str.indexOf(0));
        }
        Assert.assertTrue(str.equals("LiuXiaoHong"));

        // 切换数据库
        Assert.assertTrue(SystemManager.getInstance().createDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));

        //切换数据库之后,再检查记录个数
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        pageId = table.getPageId();
        fileId = SystemManager.getInstance().getFileId();
        Assert.assertTrue(pageId != -1);
        Assert.assertTrue(fileId != -1);
        Assert.assertEquals(2,TablePageUser.getRecordSize(tPage));

        // 非法的插入
        record[0] = "LiuXiaoHong";
        record[1] = null;

        thrown.expect(SQLRecordException.class);
        thrown.expectMessage("not null try to null");
        RecordManager.getInstance().insert(TEST_TABLE1,record);
        thrown.expect(SQLRecordException.class);
        thrown.expectMessage("not null try to null");
        RecordManager.getInstance().insert(TEST_TABLE1,fields,record);

    }

    /**
     * 大量的测试插入
     * @throws Exception
     */
    @Test
    public void testInsertMany() throws Exception {
        Table table;
        int pageId;
        int fileId;
        Object[] record;
        Page tPage;
        Page dPage;
        int total;

        // 初始化插入数据
        record = new Object[2];
        record[0] = "LiuXiaoHong";
        for (int i = 0; i < BIG_NUM; i++) {
            record[1] = i;
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }

        // 检查记录个数
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        pageId = table.getPageId();
        fileId = SystemManager.getInstance().getFileId();
        Assert.assertTrue(pageId != -1);
        Assert.assertTrue(fileId != -1);
        tPage = BufPageManager.getInstance().getPage(fileId,pageId);
        Assert.assertEquals(BIG_NUM,TablePageUser.getRecordSize(tPage));

        pageId = TablePageUser.getFirstDataPage(tPage);
        total = 0;
        for (; pageId != -1; ) {
            dPage = BufPageManager.getInstance().getPage(fileId,pageId);
            total += DataPageUser.getRecordSize(dPage);
            pageId = DataPageUser.getNextIndex(dPage);
        }
        // 检查总数
        Assert.assertEquals(total, BIG_NUM);

        // 切换数据库
        Assert.assertTrue(SystemManager.getInstance().createDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));

        // 检查总数
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        pageId = table.getPageId();
        fileId = SystemManager.getInstance().getFileId();
        tPage = BufPageManager.getInstance().getPage(fileId,pageId);
        pageId = TablePageUser.getFirstDataPage(tPage);
        total = 0;
        for (; pageId != -1; ) {
            dPage = BufPageManager.getInstance().getPage(fileId,pageId);
            total += DataPageUser.getRecordSize(dPage);
            pageId = DataPageUser.getNextIndex(dPage);
        }
        Assert.assertEquals(total, BIG_NUM);

        List<Object[]> objects = table.getAllRecords();
        Assert.assertEquals(BIG_NUM, objects.size());

    }

    /**
     * 测试插入后的取出
     */
    @Test
    public void testInsertAndGet() throws Exception {
        Table table;
        int pageId;
        int fileId;
        Object[] record;
        Object[] values;
        Page tPage;
        byte[] data;

        // 初始化插入数据
        record = new Object[2];
        record[0] = "LiuXiaoHong";
        for (int i = 0; i < 10; i++) {
            record[1] = i;
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }

        // 切换数据库
        Assert.assertTrue(SystemManager.getInstance().createDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        pageId = table.getPageId();
        fileId = SystemManager.getInstance().getFileId();
        Assert.assertTrue(pageId != -1);
        Assert.assertTrue(fileId != -1);
        tPage = BufPageManager.getInstance().getPage(fileId,pageId);

        for (int i = 0; i < 10; i++) {
            data = TablePageUser.getRecord(tPage,i);
            values = Record.bytesToValues(table,data);
            Assert.assertEquals(values[0],record[0]);
            Assert.assertEquals(values[1],i);
        }

        List<Object[]> objects = table.getAllRecords();
        Assert.assertEquals(10,objects.size());
    }
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
