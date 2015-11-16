package me.hqythu.PageFile;

import me.hqythu.util.Global;

import java.io.IOException;

/**
 * 页式文件系统缓冲管理
 */
public class BufPageManager {

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
