package me.hqythu.wxydb.test.level2;

import me.hqythu.wxydb.manager.RecordManager;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Column;
import me.hqythu.wxydb.object.DataType;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.util.BoolExpr;
import me.hqythu.wxydb.util.BoolOp;
import me.hqythu.wxydb.util.CompareOp;
import me.hqythu.wxydb.util.Where;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 15/12/26.
 */
public class WhereTest {

    public static final String TEST_DB = "test_record.db";
    public static final String TEST_NEWDB = "test_hello.db";
    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";
    public static final int NORMAL_NUM = 1000;
    public static final int BIG_NUM = 10000;

    @Before
    public void setUp() throws Exception {
        SystemManager.getInstance().createDatabase(TEST_DB);
        SystemManager.getInstance().useDatabase(TEST_DB);

        List<Column> columns = new ArrayList<>();
        // 创建表
        columns.clear();
        columns.add(new Column("name",DataType.VARCHAR,(short)20));
        columns.add(new Column("age",DataType.INT,(short)4));
        Assert.assertTrue(SystemManager.getInstance().createTable("Student",columns));

        // 创建表
        columns.clear();
        columns.add(new Column("id",DataType.INT,(short)4));
        columns.add(new Column("name",DataType.VARCHAR,(short)100));
        columns.add(new Column("sex",DataType.VARCHAR,(short)1));
        Assert.assertTrue(SystemManager.getInstance().createTable("Customer",columns));

        // 初始化插入数据
        List<Object> record = new ArrayList<>();
        record.add("LiuXiaoHong");
        record.add(0);
        for (int i = 0; i < NORMAL_NUM; i++) {
            record.set(1,i);
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }
    }

    @After
    public void tearDown() throws Exception {
        SystemManager.getInstance().dropDatabase(TEST_DB);
    }

    /**
     * 一个bool表达式
     */
    @Test
    public void testWhere1() throws Exception {
        Where where = new Where();
        // age >= 5
        where.boolExprsAndOps.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.GEQ,5,true));
        where.isExprs.add(true);
        RecordManager.getInstance().remove(TEST_TABLE1,where);

        Table table = SystemManager.getInstance().getTable(TEST_TABLE1);
        List<Object[]> records = table.getAllRecords();
        Assert.assertEquals(5,records.size());

    }

    /**
     * 两个或两个以上bool表达式
     *
     */
    @Test
    public void testWhere2() throws Exception {
        Table table;
        List<Object[]> records;
        Where where = new Where();

        // 删除 age < 18 || age >= 25
        where.boolExprsAndOps.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.LES,18,true));
        where.isExprs.add(true);
        where.boolExprsAndOps.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.GEQ,25,true));
        where.isExprs.add(true);
        where.boolExprsAndOps.add(BoolOp.OR);
        where.isExprs.add(false);
        RecordManager.getInstance().remove(TEST_TABLE1,where);

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(7,records.size());

        where.clear();
        where.boolExprsAndOps.add(new BoolExpr(true));
        where.isExprs.add(true);
        RecordManager.getInstance().remove(TEST_TABLE1,where);

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(0,records.size());
    }

    /**
     * 删除,插入,再删除
     */
    @Test
    public void testWhere3() throws Exception {
        Table table;
        List<Object[]> records;

        // 初始化插入数据
        List<Object> record = new ArrayList<>();
        record.add("LiuXiaoHong");
        record.add(0);

        // 删除 age大于等于10
        Where where = new Where();
        where.boolExprsAndOps.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.GEQ,10,true));
        where.isExprs.add(true);
        RecordManager.getInstance().remove(TEST_TABLE1,where);
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(10,records.size());

        // 插入
        for (int i = 0; i < NORMAL_NUM; i++) {
            record.set(1,i+5);
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }

        RecordManager.getInstance().remove(TEST_TABLE1,where);

        records = table.getAllRecords();
        Assert.assertEquals(15,records.size());
    }
}
