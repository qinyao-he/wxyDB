package me.hqythu.wxydb.test.level2;

import me.hqythu.wxydb.manager.SystemManager;
import me.hqythu.wxydb.object.Column;
import me.hqythu.wxydb.object.DataType;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.pagefile.BufPageManager;
import me.hqythu.wxydb.pagefile.Page;
import me.hqythu.wxydb.pagefile.TablePageUser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

        List<Column> columns = new ArrayList<>();
        // 创建表
        columns.clear();
        columns.add(new Column("name",DataType.VARCHAR,(short)40));
        columns.add(new Column("age",DataType.INT,(short)4));
        Assert.assertTrue(SystemManager.getInstance().createTable("Student",columns));

        Table table;
        int pageId;
        int fileId;
        int size;
        Page tpage;

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
