package me.hqythu.system;

import me.hqythu.exception.SQLExecException;
import me.hqythu.record.FilePageManager;

import java.io.File;

public class SystemManager {

    String connectDB = null;
    int fileId;

    /**
     * 创建DB
     *
     * @param DBname
     * @return
     */
    public boolean createDatabase(String DBname) {
        try {
            File file = new File(DBname);
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除DB
     *
     * @param DBname
     * @return
     */
    public boolean dropDatabase(String DBname) {
        File file = new File(DBname);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 选择DB
     *
     * @param DBname
     */
    public void useDatabase(String DBname) {
        if (connectDB == null) {
            connectDB = DBname;
            fileId = FilePageManager.getInstance().openFile(DBname);
        } else if (connectDB.equals(DBname)) {
            // 已经在使用
        } else {
            FilePageManager.getInstance().closeFile(fileId);
            connectDB = DBname;
            fileId = FilePageManager.getInstance().openFile(DBname);
        }
    }

    public static void main(String[] args) {
        SystemManager manager = new SystemManager();
        manager.createDatabase("test.db");
    }
}
