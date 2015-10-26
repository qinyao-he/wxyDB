package me.hqythu.record;

import java.io.IOException;

/**
 * 页式文件系统缓冲管理
 */
public class BufPageManager {

    public static final int MAX_CACHE_SIZE = 65536;

    LRUCache<Long, Page> cache;

    public BufPageManager(int capacity) {
        if (capacity > MAX_CACHE_SIZE) {
            cache = new LRUCache<>(MAX_CACHE_SIZE);
        } else {
            cache = new LRUCache<>(capacity);
        }
    }

    public Page getPage(int fileId, int pageId) throws IOException {
        long index = hash(fileId, pageId);
        Page page = cache.get(index);
        if (page == null) {
            byte[] data = FilePageManager.getInstance().readPage(fileId, pageId);
            page = new Page(fileId, pageId, data);
            cache.put(hash(page),page);
        }
        return page;
    }

    public void realsePage(Page page) throws IOException {
        page.writeBack();
        cache.remove(hash(page));
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
