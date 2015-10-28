package me.hqythu.record;

import me.hqythu.Global;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 页式文件系统缓冲管理
 */
public class BufPageManager {

    /**
     * 持久化cache
     * 数据库文件第一页
     * 表信息页
     */
    Map<Long, Page> primaryCache = null;
    /**
     * 非持久化cache
     * 普通数据页
     */
    LRUCache cache;

    private static BufPageManager manager = null;

    public static BufPageManager getInstance() {
        if (manager == null) {
            manager = new BufPageManager();
        }
        return manager;
    }

    private BufPageManager() {
        cache = new LRUCache(Global.MAX_CACHE_SIZE);
        primaryCache = new HashMap<>();
    }

    /**
     * 持久化的页
     */
    public Page getPrimaryPage(int fileId, int pageId) throws IOException {
        long index = hash(fileId, pageId);
        Page page = primaryCache.get(index);
        if (page == null) {
            byte[] data = FilePageManager.getInstance().readPage(fileId, pageId);
            page = new Page(fileId, pageId, data);
            primaryCache.put(index, page);
        }
        return page;
    }

    /**
     * 非持久化的页
     */
    public Page getPage(int fileId, int pageId) throws IOException {
        long index = hash(fileId, pageId);
        Page page = cache.get(index);
        if (page == null) {
            byte[] data = FilePageManager.getInstance().readPage(fileId, pageId);
            page = new Page(fileId, pageId, data);
            cache.put(index, page);
        }
        return page;
    }

    public void releasePage(Page page) {
        cache.remove(hash(page));
    }

    public void clear() {
        cache.clear();
        primaryCache.values().forEach(me.hqythu.record.Page::writeBack);
        primaryCache.clear();
    }

    private long hash(Page page) {
        return hash(page.getFileId(), page.getPageId());
    }

    private long hash(int fileId, int pageId) {
        return (fileId << 32) + pageId;
    }

    public static void main(String[] args) throws IOException {

    }
}
