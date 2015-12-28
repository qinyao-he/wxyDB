package me.hqythu.wxydb.manager;

import me.hqythu.wxydb.exception.level1.SQLSystemException;
import me.hqythu.wxydb.object.Table;
import me.hqythu.wxydb.pagefile.*;
import me.hqythu.wxydb.util.Global;
import me.hqythu.wxydb.object.Column;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemManager {

    // connectDB是否为null，决定其他是否为null
    String connectDB = null;
    Map<String, Table> tables = null;
    int fileId = -1;
    private static SystemManager manager = null;

    private SystemManager() {
    }

    public static SystemManager getInstance() {
        if (manager == null) {
            manager = new SystemManager();
        }
        return manager;
    }

    //--------------------DML support--------------------

    /**
     * 创建DB
     */
    public boolean createDatabase(String DBname) throws SQLSystemException {
        if (connectDB != null && connectDB.equals(DBname)) { // 已经存在
            return false;
        }
        try {
            File file = new File(DBname);
            if (!file.createNewFile()) return false;
            int tempFileId = FilePageManager.getInstance().openFile(DBname);

            // 数据库文件第一页初始化
            Page page = BufPageManager.getInstance().getPage(tempFileId, 0);
            DbPageUser.initDbPage(page);
            BufPageManager.getInstance().releasePage(page);
            FilePageManager.getInstance().closeFile(tempFileId);

            return true;
        } catch (Exception e) {
            throw new SQLSystemException(e.getMessage());
        }
    }

    /**
     * 删除DB
     */
    public boolean dropDatabase(String DBname) {
        if (connectDB != null && connectDB.equals(DBname)) {
            closeDatabase();
        }
        File file = new File(DBname);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 选择DB
     */
    public boolean useDatabase(String DBname) {
        if (DBname == null) return false;
        if (connectDB != null && connectDB.equals(DBname)) {
            return true;
        }
        closeDatabase();
        return openDatabase(DBname);
    }


    /**
     * 创建表
     */
    public boolean createTable(String tableName, List<Column> columns) throws SQLSystemException {
        try {
            Column[] cols = new Column[columns.size()];
            columns.toArray(cols);
            return createTable(tableName, cols);
        } catch (Exception e) {
            throw new SQLSystemException(e.getMessage());
        }
    }

    /**
     * 删除表
     */
    public boolean dropTable(String tableName) {
        if (connectDB == null) return false;

        Table table = tables.get(tableName);
        if (table == null) return false;

        try {
            Page dbPage = getDbPage();

            // 清空表的记录
            table.removeAll();
            // 从SystemManager缓存的map中删除
            tables.remove(tableName);
            // 库页中删除表的信息
            int pageId = DbPageUser.delTableInfo(dbPage, tableName);
            // 回收页
            DbPageUser.recyclePage(dbPage, pageId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SHOW TABLES 命令
     */
    public String showTables() {
        if (tables == null) return "";
        Object[] tableNames = tables.keySet().toArray();
        return Arrays.toString(tableNames);
    }

    /**
     * DESC 命令
     */
    public String showTableColumns(String tableName) {
        Table table = getTable(tableName);
        Column[] columns = table.getColumns();
        StringBuilder builder = new StringBuilder(1024);
        builder.append('[');
        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i].toString());
            if (i != columns.length - 1) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    /**
     * 结束,写回
     */
    public void close() {
        closeDatabase();
    }

    //--------------------为其他模块提供系统管理--------------------
    public Table getTable(String tableName) {
        if (connectDB == null) return null;
        return tables.get(tableName);
    }

    public Map<String, Table> getTables() {
        return tables;
    }

    public Page getDbPage() {
        try {
            return BufPageManager.getInstance().getPage(fileId, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public int getFileId() {
        return fileId;
    }

    //--------------------内部辅助函数--------------------
    protected void closeDatabase() {
        if (connectDB != null) {
            connectDB = null;
            tables.clear();
            BufPageManager.getInstance().clear();
            FilePageManager.getInstance().closeFile(fileId);
            fileId = -1;
        }
    }

    protected boolean openDatabase(String DBname) {
        fileId = FilePageManager.getInstance().openFile(DBname);
        if (fileId == -1) return false;
        connectDB = DBname;

        try {
            // 切换DB的初始化
            Page dbPage = BufPageManager.getInstance().getPage(fileId, 0);
            tables = new HashMap<>();
            DbPageUser.initTableFromPage(dbPage, tables);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean createTable(String tableName, Column[] columns) throws SQLSystemException {
        if (connectDB == null) return false;
        if (tables.size() >= Global.TABLE_MAX_SIZE) return false; // 限制一个库的表数
        if (tableName.length() > Global.TABLE_NAME_LEN) { // 表名
            return false;
        }
        if (tables.containsKey(tableName)) { // 已经存在
            return false;
        }
        for (Column column : columns) {
            if (column.name.length() > Global.COL_NAME_LEN) return false;
        }
        try {
            Page dbPage = getDbPage();
            // 分配新页
            Page tablePage = DbPageUser.getNewPage(dbPage);
            // 数据库页中增加一个表信息
            assert tablePage != null;
            DbPageUser.addTableInfo(dbPage, tableName, tablePage.getPageId());
            // 初始化表首页
            TablePageUser.initPage(tablePage, tableName, columns);
            // 系统管理添加表
            Table table = TablePageUser.getTable(tablePage);
            tables.put(tableName, table);
            return true;
        } catch (Exception e) {
            throw new SQLSystemException(e.getMessage());
        }
    }

}
