package me.hqythu.manager;

import me.hqythu.object.Table;
import me.hqythu.util.Global;
import me.hqythu.pagefile.*;
import me.hqythu.object.Column;

import java.io.*;
import java.util.HashMap;
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


    /**
     * 创建DB
     */
    public boolean createDatabase(String DBname) {
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
            e.printStackTrace();
            return false;
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
     * 关闭DB
     */
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

    /**
     * 创建表
     */
    public boolean createTable(String tableName, Column[] columns) {
        if (connectDB == null) return false;
        if (tables.size() >= Global.TABLE_MAX_SIZE) return false;
        if (tableName.length() > Global.TABLE_NAME_LEN) {
            return false;
        }
        for (Column column : columns) {
            if (column.name.length() > Global.COL_NAME_LEN) return false;
        }

        try {
            Page dbPage = BufPageManager.getInstance().getPage(fileId, 0);

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
            e.printStackTrace();
            return false;
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
            Page dbPage = BufPageManager.getInstance().getPage(fileId, 0);

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
     * 显示表
     */
    public Object[] showTable() {
        if (connectDB == null) return null;
        return tables.keySet().toArray();
    }

    /**
     * 获取表
     */
    public Table getTable(String tableName) {
        if (connectDB == null) return null;
        return tables.get(tableName);
    }

    public Page getDbPage() {
        try {
            return BufPageManager.getInstance().getPage(fileId,0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public int getFileId() {
        return fileId;
    }
}
