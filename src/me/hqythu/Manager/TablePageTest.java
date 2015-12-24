package me.hqythu.manager;

import me.hqythu.object.Column;
import me.hqythu.object.DataType;
import me.hqythu.object.Table;
import me.hqythu.pagefile.BufPageManager;
import me.hqythu.pagefile.Page;
import me.hqythu.pagefile.TablePageUser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by apple on 15/12/24.
 */
public class TablePageTest {
    public static final String TEST_DB = "test.db";
    public static final String TEST_NEWDB = "newdb.db";
    public static final String TEST_OTHERDB = "hello.db";

    public static final String TEST_TABLE1 = "Student";
    public static final String TEST_TABLE2 = "Customer";

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
    public void testTablePage() throws Exception {

        // 创建表
        Column columns[] = new Column[2];
        columns[0] = new Column("name", DataType.VARCHAR,(short)40);
        columns[1] = new Column("age",DataType.INT,(short)4);
        Assert.assertTrue(SystemManager.getInstance().createTable(TEST_TABLE1,columns));

        Table table;
        int pageId;
        int fileId;
        int size;
        Object[] record;
        String fields[];
        int[] cols;
        Page tpage;

        // 初始化插入数据
        record = new Object[2];
        record[0] = "LiuXiaoHong";
        record[1] = 18;
        fields = new String[]{"name","age"};
        cols = new int[]{0,1};
        Column[] tcols;
        Column col;

        // 检查列数
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        fileId = SystemManager.getInstance().getFileId();
        pageId = table.getPageId();
        tpage = BufPageManager.getInstance().getPage(fileId,pageId);
        Assert.assertTrue(2 == TablePageUser.getColumnSize(tpage));

        // 切换数据库,使得页面writeback
        Assert.assertTrue(SystemManager.getInstance().createDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().dropDatabase(TEST_NEWDB));
        Assert.assertTrue(SystemManager.getInstance().useDatabase(TEST_DB));

        // 切换数据库后再次检查列数
        table = SystemManager.getInstance().getTable(TEST_TABLE1);
        fileId = SystemManager.getInstance().getFileId();
        pageId = table.getPageId();
        tpage = BufPageManager.getInstance().getPage(fileId,pageId);
        size = TablePageUser.getColumnSize(tpage);
        Assert.assertSame(2,size);
        col = TablePageUser.getColumn(tpage,0);
        Assert.assertTrue(col.name.equals("name"));
        col = TablePageUser.getColumn(tpage,1);
        Assert.assertTrue(col.name.equals("age"));
    }
}
