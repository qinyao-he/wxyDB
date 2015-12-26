package me.hqythu.test;

import me.hqythu.manager.RecordManager;
import me.hqythu.manager.SystemManager;
import me.hqythu.object.Column;
import me.hqythu.object.DataType;
import me.hqythu.object.Table;
import me.hqythu.util.BoolExpr;
import me.hqythu.util.BoolOp;
import me.hqythu.util.CompareOp;
import me.hqythu.util.Where;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

        // 初始化插入数据
        Object[] record = new Object[2];
        record[0] = "LiuXiaoHong";
        for (int i = 0; i < NORMAL_NUM; i++) {
            record[1] = i;
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
        where.boolExprs.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.GEQ,5,true));
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
        where.boolExprs.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.LES,18,true));
        where.boolExprs.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.GEQ,25,true));
        where.boolOps.add(BoolOp.OR);
        RecordManager.getInstance().remove(TEST_TABLE1,where);

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(7,records.size());

        where.clear();
        where.boolExprs.add(new BoolExpr());
        RecordManager.getInstance().remove(TEST_TABLE1,where);

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(0,records.size());
    }

    @Test
    public void testWhere3() throws Exception {
        Where where = new Where();
        where.boolExprs.add(new BoolExpr());
        RecordManager.getInstance().remove(TEST_TABLE1,where);

        Table table = SystemManager.getInstance().getTable(TEST_TABLE1);
        List<Object[]> records = table.getAllRecords();
        Assert.assertEquals(0,records.size());
    }

    /**
     * 删除,插入,再删除
     */
    @Test
    public void testWhere4() throws Exception {
        Table table;
        List<Object[]> records;

        // 初始化插入数据
        Object[] record = new Object[2];
        record[0] = "LiuXiaoHong";

        // 删除 age大于等于10
        Where where = new Where();
        BoolExpr expr = new BoolExpr();
        expr.tableNameL = TEST_TABLE1;
        expr.columnNameL = "age";
        expr.compareOp = CompareOp.GEQ; // <
        expr.valueR = 10;
        where.boolExprs.add(expr);
        RecordManager.getInstance().remove(TEST_TABLE1,where);
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(10,records.size());

        // 插入
        for (int i = 0; i < NORMAL_NUM; i++) {
            record[1] = i+5;
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }

        RecordManager.getInstance().remove(TEST_TABLE1,where);

        records = table.getAllRecords();
        Assert.assertEquals(15,records.size());
    }
}
