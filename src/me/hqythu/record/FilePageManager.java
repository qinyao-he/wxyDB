package me.hqythu.record;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * 页式文件系统操作
 * 创建、删除、打开、关闭文件
 * 读写页
 */
public class FilePageManager {

    public static final int PAGE_BYTE_SIZE = 8192;
    public static final int PAGE_SIZE_IDX = 13; // 2^13 = 8192
    public static final int MAX_FILE_NUM = 128;

    private static FilePageManager manager = null;
    // 文件位图
    BitSet fileBitMap;
    RandomAccessFile[] files;


    /**
     * 单例模式
     */
    public static FilePageManager getInstance() {
        if (manager == null) {
            manager = new FilePageManager();
        }
        return manager;
    }

    private FilePageManager() {
        fileBitMap = new BitSet(MAX_FILE_NUM);
        files = new RandomAccessFile[MAX_FILE_NUM];
        fileBitMap.clear();
    }

    /**
     * 打开文件
     *
     * @param fileName
     * @return 文件ID
     */
    public int openFile(String fileName) {
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "rw");
            int fileId = fileBitMap.nextClearBit(0);
            files[fileId] = file;
            fileBitMap.set(fileId);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 关闭文件
     *
     * @param fileId
     */
    public void closeFile(int fileId) {
        try {
            files[fileId].close();
            fileBitMap.clear(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     */
    public boolean createFile(String fileName) {
        try {
            File file = new File(fileName);
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /**
     * 读文件的某一页
     *
     * @param fileId
     * @param pageId 从0开始
     * @return byte[] 读到的内容
     * @throws Exception
     */
    public byte[] readPage(int fileId, int pageId) throws IOException {
        byte[] buf = new byte[PAGE_BYTE_SIZE];
        RandomAccessFile file = files[fileId];
        file.seek(pageId << PAGE_SIZE_IDX);
        file.read(buf);
        return buf;
    }

    /**
     * 写文件的某一页
     *
     * @param buf 写入的内容
     */
    public void writePage(int fileId, int pageId, byte[] buf) throws IOException {
        RandomAccessFile file = files[fileId];
        file.seek(pageId << PAGE_SIZE_IDX);
        file.write(buf, 0, PAGE_BYTE_SIZE);
    }

    /**
     * 关闭
     * 关闭所有文件
     * 清空bitMap
     */
    public void shutdown() {
        while (true) {
            int fileId = fileBitMap.nextSetBit(0);
            if (fileId >= 0) {
                try {
                    files[fileId].close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    fileBitMap.clear(fileId);
                }
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) {

    }
}

