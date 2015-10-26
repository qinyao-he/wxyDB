package me.hqythu.record;

import java.io.IOException;

/**
 * 页
 */
class Page {
    private int fileId;
    private int pageId;
    private boolean dirty;
    private byte[] data;

    public Page(int fileId, int pageId, byte[] data) {
        this.fileId = fileId;
        this.pageId = pageId;
        this.dirty = false;
        this.data = data;
    }

    /**
     * 返回页数据
     * 用于读写
     *
     * @return
     */
    public byte[] getData() {
        return data;
    }

    /**
     * 标记为dirty
     * 进行写操作后，标记为dirty才能写回
     */
    public void setDirty() {
        dirty = true;
    }

    /**
     * 写回
     * 建议在页释放的时候执行
     *
     * @throws IOException
     */
    public void writeBack() throws IOException {
        if (dirty) {
            FilePageManager.getInstance().writePage(fileId, pageId, data);
            dirty = false;
        }
    }

    public int getFileId() {
        return fileId;
    }

    public int getPageId() {
        return pageId;
    }

    /**
     * 垃圾回收的时候写回
     */
    @Override
    protected void finalize() {
        try {
            writeBack();
        } catch (Exception e) {
        }
    }
}
