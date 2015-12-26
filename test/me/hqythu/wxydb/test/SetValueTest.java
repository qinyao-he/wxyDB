package me.hqythu.wxydb.test;

import me.hqythu.wxydb.manager.RecordManager;
import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Column;
import me.hqythu.wxydb.object.DataType;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.util.BoolExpr;
import me.hqythu.wxydb.util.CalcOp;
import me.hqythu.wxydb.util.SetValue;
import me.hqythu.wxydb.util.Where;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by apple on 15/12/26.
 */
public class SetValueTest {
    public static final String TEST_DB = "test_record.db";
    public static final String TEST_NEWDB = "test_hello.db";
    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";
    public static final int NORMAL_NUM = 10;
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

    @Test
    public void testSetValue() throws Exception {
        Table table;
        List<Object[]> records;

        // Where 初始化
        Where where = new Where();
        where.boolExprsAndOps.add(new BoolExpr(true));
        where.isExprs.add(true);
//        where.boolExprs.add(new BoolExpr());
        List<SetValue> setValues = new ArrayList<>();

        // 更新,set age = age+1
        setValues.add(new SetValue("age",CalcOp.ADD,1,true));
        RecordManager.getInstance().update(TEST_TABLE1,where,setValues);

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(NORMAL_NUM,records.size());

        for (int i = 0; i < records.size(); i++) {
            Assert.assertEquals(i+1,records.get(i)[1]);
        }

        // 更新,age = age+1
        setValues.add(new SetValue("age",14));
        RecordManager.getInstance().update(TEST_TABLE1, where, setValues);

        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        records = table.getAllRecords();
        Assert.assertEquals(NORMAL_NUM, records.size());

        for (Object[] record : records) {
            Assert.assertEquals(14, record[1]);
        }

    }
}
