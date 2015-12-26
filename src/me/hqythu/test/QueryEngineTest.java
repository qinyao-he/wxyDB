package me.hqythu.test;

import me.hqythu.manager.QueryEngine;
import me.hqythu.manager.RecordManager;
import me.hqythu.manager.SystemManager;
import me.hqythu.object.Column;
import me.hqythu.object.DataType;
import me.hqythu.object.Table;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/26.
 */
public class QueryEngineTest {


    public static final String TEST_DB = "test_query.db";
    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";
    public static final int NORMAL_NUM = 100;
    public static final int BIG_NUM = 10000;
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

    @Test
    public void testTableJoin() throws Exception {
        Object[] record;
        List<Map<Table,Object[]>> results;
        List<String> tableNames;

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

        tableNames = new ArrayList<>();

        tableNames.add(TEST_TABLE1);
        results = QueryEngine.getInstance().tableJoinRecords(tableNames);
        Assert.assertEquals(NUM1,results.size());

        tableNames.add(TEST_TABLE2);
        results = QueryEngine.getInstance().tableJoinRecords(tableNames);
        Assert.assertEquals(NUM1*NUM2,results.size());

    }
}
