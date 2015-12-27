package me.hqythu.wxydb.test;

import me.hqythu.wxydb.manager.QueryEngine;
import me.hqythu.wxydb.manager.RecordManager;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Column;
import me.hqythu.wxydb.object.DataType;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.util.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Created by apple on 15/12/26.
 */
public class QueryEngineTest {


    public static final String TEST_DB = "test_query.db";
    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";
    public static final int BIG_NUM = 10000;
    public static final int NORMAL_NUM = 100;
    public static final int NUM1 = 100;
    public static final int NUM2 = 200;

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
     * 测试多表连接,小数据
     */
    @Test
    public void testTableJoin() throws Exception {
        Object[] record;
        List<Map<Table,Object[]>> results;
        Set<String> tableNames;

        // 初始化插入数据
        // 表1
        record = new Object[2];
        record[0] = "LiuXiaoHong";
        for (int i = 0; i < NUM1; i++) {
            record[1] = i;
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }
        // 表2
        record = new Object[3];
        record[1] = "LiuXiaoHong";
        record[2] = "F";
        for (int i = 0; i < NUM2; i++) {
            record[0] = i;
            RecordManager.getInstance().insert(TEST_TABLE2,record);
        }

        tableNames = new HashSet<>();

        tableNames.add(TEST_TABLE1);
        results = QueryEngine.getInstance().tableJoinRecords(tableNames);
        Assert.assertEquals(NUM1,results.size());

        tableNames.add(TEST_TABLE2);
        results = QueryEngine.getInstance().tableJoinRecords(tableNames);
        Assert.assertEquals(NUM1*NUM2,results.size());

    }

    /**
     * 测试多表连接,一个表为空
     */
    @Test
    public void testNormal() throws Exception {
        Object[] record;
        List<Map<Table,Object[]>> results;
        Set<String> tableNames;

        // 初始化插入数据
        // 表1
        record = new Object[2];
        record[0] = "LiuXiaoHong";
        for (int i = 0; i < BIG_NUM; i++) {
            record[1] = i;
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }

        tableNames = new HashSet<>();

        tableNames.add(TEST_TABLE1);
        results = QueryEngine.getInstance().tableJoinRecords(tableNames);
        Assert.assertEquals(BIG_NUM,results.size());

        tableNames.add(TEST_TABLE2);
        results = QueryEngine.getInstance().tableJoinRecords(tableNames);
        Assert.assertEquals(0,results.size());
    }

    /**
     * 测试查询
     */
    @Test
    public void testQuery() throws Exception {
        Object[] record;
        List<Map<Table,Object[]>> results;
        List<Object[]> queryset;
        Set<String> tableNames;
        SelectOption select;
        Where where;

        // 初始化插入数据
        // 表1
        record = new Object[2];
        record[0] = "LiuXiaoHong";
        for (int i = 0; i < 10; i++) {
            record[1] = i;
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }

        record = new Object[3];
        record[1] = "LiuXiaoHong";
        record[2] = "F";
        for (int i = 0; i < 2; i++) {
            record[0] = i;
            RecordManager.getInstance().insert(TEST_TABLE2,record);
        }

        select = new SelectOption();
        select.tableNames.add(TEST_TABLE1);
        select.fromTableNames.add(TEST_TABLE1);
        select.columnNames.add("age");
        select.tableNames.add(TEST_TABLE2);
        select.fromTableNames.add(TEST_TABLE2);
        select.columnNames.add("id");

        where = new Where();
        where.boolExprsAndOps.add(new BoolExpr(TEST_TABLE1,"age", CompareOp.GEQ, 7, true));
        where.isExprs.add(true);
        queryset = QueryEngine.getInstance().query(select, where);
        Assert.assertEquals(6,queryset.size());
    }

    /**
     * 测试聚集查询
     */
    @Test
    public void testFunc() throws Exception {
        Object[] record;
        double temp;
        double avg,sum,min,max;
        Func func;

        // 初始化插入数据
        // 表1
        record = new Object[2];
        record[0] = "LiuXiaoHong";
        avg = 0;
        sum = 0;
        for (int i = 0; i < 10; i++) {
            avg += i;
            sum += i;
            record[1] = i;
            RecordManager.getInstance().insert(TEST_TABLE1,record);
        }
        avg /= 10;

        record = new Object[3];
        record[0] = 0;
        record[1] = "LiuXiaoHong";
        record[2] = "F";
        RecordManager.getInstance().insert(TEST_TABLE2,record);

        func = Func.AVG;
        temp = QueryEngine.getInstance().func(func,TEST_TABLE1, "age");
        Assert.assertTrue(abs(avg-temp) < 1e6);
        func = Func.SUM;
        temp = QueryEngine.getInstance().func(func,TEST_TABLE1, "age");
        Assert.assertTrue(abs(sum-temp) < 1e6);
        max = 9;
        func = Func.MAX;
        temp = QueryEngine.getInstance().func(func,TEST_TABLE1, "age");
        Assert.assertTrue(abs(max-temp) < 1e6);
        min = 0;
        func = Func.MIN;
        temp = QueryEngine.getInstance().func(func,TEST_TABLE1, "age");
        Assert.assertTrue(abs(min-temp) < 1e6);
    }
}
