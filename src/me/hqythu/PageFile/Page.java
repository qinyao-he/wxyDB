package me.hqythu.pagefile;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 页
 */
public class Page {
    protected int fileId;
    protected int pageId;
    public boolean dirty;
    protected byte[] data;
    protected ByteBuffer buffer;

    public Page(int fileId, int pageId, byte[] data) {
        this.fileId = fileId;
        this.pageId = pageId;
        this.dirty = false;
        this.data = data;
        buffer = ByteBuffer.wrap(data);
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
    public ByteBuffer getBuffer() {
        return buffer;
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
    public void writeBack() {
        if (dirty) {
            try {
//                System.out.println(String.format("write file:%d page:%d",fileId,pageId));
                FilePageManager.getInstance().writePage(fileId, pageId, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dirty = false;
        }
    }

    public int getFileId() {
        return fileId;
    }

    public int getPageId() {
        return pageId;
    }

}
