package me.hqythu.system;

import me.hqythu.record.FilePageManager;

import java.io.File;

public class SystemManager {

    /**
     * 创建DB
     * @param DBname
     * @return
     */
    public boolean createDatabase(String DBname) {
        return FilePageManager.getInstance().createFile(DBname);
    }

    /**
     * 删除DB
     * @param DBname
     * @return
     */
    public boolean dropDatabase(String DBname) {
        return FilePageManager.getInstance().deleteFile(DBname);
    }

    /**
     * 选择DB
     * @param DBname
     */
    public void useDatabase(String DBname) {

    }

    public static void main(String[] args) {
        SystemManager manager = new SystemManager();
        manager.createDatabase("test.db");
    }
}
