package me.hqythu.PageFile;

import me.hqythu.util.Global;

import java.nio.ByteBuffer;

public class DataPageUser {
    private DataPageUser(){}

    public static void initDataPage(Page page, short recordLen) {
        byte[] data = page.getData();
        ByteBuffer buffer = page.getBuffer();
        buffer.position(0);
        buffer.putInt(Global.DTPAGE_IDX_POS, page.getPageId());  // 该页索引号
        buffer.putInt(Global.DTPAGE_LASTIDX_POS, -1);            // 上一页索引
        buffer.putInt(Global.DTPAGE_NEXTIDX_POS, -1);            // 下一页索引
        buffer.putShort(Global.DTPAGE_PROP_POS, (short) 0);      // 属性
        buffer.putShort(Global.DTPAGE_RECORDLEN_POS, recordLen); // 记录的长度
        page.setDirty();
    }

    /**
     * 获取该数据页上的一个记录
     * @param index 在该页中的位置,从[0,size)
     */
    public static byte[] readRecord(Page page, int index) {
        ByteBuffer buffer = page.getBuffer();
        short recordLen = buffer.getShort(Global.DTPAGE_RECORDLEN_POS);
        int pos = Global.DTPAGE_DATA_POS + index * recordLen;
        byte[] record = new byte[recordLen];
        buffer.position(pos);
        buffer.get(record);
        return record;
    }

    /**
     * 往数据页上写一个记录
     * @param record 记录
     */
    public static boolean writeRecord(Page page, byte[] record) {
        ByteBuffer buffer = page.getBuffer();
        int size = buffer.getInt(Global.DTPAGE_SIZE_POS);
        short recordLen = buffer.getShort(Global.DTPAGE_RECORDLEN_POS);
        int pos = Global.DTPAGE_DATA_POS + size * recordLen;
        if (pos + record.length > Global.PAGE_BYTE_SIZE) {
            return false;
        }
        buffer.position(pos);
        buffer.put(record);
        size++;
        buffer.putInt(Global.DTPAGE_SIZE_POS, size);
        page.setDirty();
        return true;
    }

    /**
     * 删除记录
     * 最后一个挪到被删除位置
     */
    public static void removeRecord(Page page, int index) {
        byte[] data = page.getData();
        ByteBuffer buffer = page.getBuffer();
        short recordLen = buffer.getShort(Global.DTPAGE_RECORDLEN_POS);
        int size = buffer.getInt(Global.DTPAGE_SIZE_POS);
        if (index != (size - 1)) {
            int posTo = Global.DTPAGE_DATA_POS + index * recordLen;
            int posFrom = Global.DTPAGE_DATA_POS + (size - 1) * recordLen;
            System.arraycopy(data,posFrom,data,posTo,recordLen);
        }
        size--;
        buffer.putInt(Global.DTPAGE_SIZE_POS,size);
        page.setDirty();
    }

    /**
     * 连着两个数据页
     */
    public static void connectPage(Page page1, Page page2) {
        ByteBuffer buffer1 = page1.getBuffer();
        ByteBuffer buffer2 = page2.getBuffer();

        buffer1.putInt(Global.DTPAGE_NEXTIDX_POS, page2.getPageId());
        buffer2.putInt(Global.DTPAGE_LASTIDX_POS, page1.getPageId());
        page1.setDirty();
        page2.setDirty();
    }

    /**
     * 断开数据页的连接
     */
    public static void removeConnectPage(Page page) {
        ByteBuffer buffer = page.getBuffer();
        int fileId = page.getFileId();
        try {
            int lastPageIndex = buffer.getInt(Global.DTPAGE_LASTIDX_POS);
            if (lastPageIndex != -1) {
                Page lastPage = BufPageManager.getInstance().getPage(fileId,lastPageIndex);
                ByteBuffer lastBuffer = lastPage.getBuffer();
                lastBuffer.putInt(Global.DTPAGE_NEXTIDX_POS, page.getPageId());
            }
            int nextPageIndex = buffer.getInt(Global.DTPAGE_NEXTIDX_POS);
            if (nextPageIndex != -1) {
                Page nextPage = BufPageManager.getInstance().getPage(fileId,nextPageIndex);
                ByteBuffer nextBuffer = nextPage.getBuffer();
                nextBuffer.putInt(Global.DTPAGE_LASTIDX_POS, lastPageIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据页是否存满记录
     */
    public static boolean isFull(Page page) {
        ByteBuffer buffer = page.getBuffer();
        int size = buffer.getInt(Global.DTPAGE_SIZE_POS);
        int cap = buffer.getInt(Global.DTPAGE_CAP_POS);
        return size < cap;
    }

    /**
     * 下一页
     */
    public static int getNextPageId(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_NEXTIDX_POS);
    }

    /**
     * 数据页存放记录的个数
     */
    public static int getRecordSize(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_SIZE_POS);
    }


}
