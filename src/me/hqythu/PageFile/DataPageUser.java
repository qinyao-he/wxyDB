package me.hqythu.pagefile;

import me.hqythu.util.Global;

import java.nio.ByteBuffer;

public class DataPageUser {

    public static final int DATA_PROP_HASPRIMARY = 0x8000;

    private DataPageUser(){}

    /**
     * 初始化数据页
     * @param page
     * @param recordLen 每个记录的长度
     */
    public static void initPage(Page page, int recordLen) {
        setIndex(page,page.getPageId());    // 该页索引号
        setPreIndex(page,-1);               // 上一页索引
        setNextIndex(page,-1);              // 下一页索引
        setProp(page,0);                    // 属性
        setRecordLen(page,recordLen);       // 记录的长度
        setRecordSize(page,0);              // 记录的个数
        setCapacity(page,(Global.DTPAGE_DATA_LEN/recordLen)); // 容量
        page.setDirty();
    }

    /**
     * 获取该数据页上的一个记录
     * @param index 在该页中的位置,从[0,size)
     */
    public static byte[] readRecord(Page page, int index) {
        ByteBuffer buffer = page.getBuffer();
        int recordLen = getRecordLen(page);
        int size = getRecordSize(page);

        if (index >= size) return null;
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
    public static boolean addRecord(Page page, byte[] record) {
        ByteBuffer buffer = page.getBuffer();
        int size = getRecordSize(page);
        int recordLen = getRecordLen(page);

        if (isFull(page)) return false;
        if (record.length != recordLen) return false;
        int pos = Global.DTPAGE_DATA_POS + size * recordLen;
        buffer.position(pos);
        buffer.put(record);
        incRecordSize(page);

        page.setDirty();
        return true;
    }

    /**
     * 写记录
     */
    public static boolean writeRecord(Page page, int index, byte[] record) {
        byte[] data = page.getData();
        int size = getRecordSize(page);
        int recordLen = getRecordLen(page);

        if (index >= size) return false;
        if (record.length != recordLen) return false;
        int pos = Global.DTPAGE_DATA_POS + index * recordLen;
        System.arraycopy(data,pos,record,0,recordLen);

        page.setDirty();
        return true;
    }

    /**
     * 删除记录
     * 最后一个挪到被删除位置
     */
    public static void removeRecord(Page page, int index) {
        byte[] data = page.getData();
        int size = getRecordSize(page);
        int recordLen = getRecordLen(page);

        if (index >= size) return;
        if (index != (size - 1)) {
            int posTo = Global.DTPAGE_DATA_POS + index * recordLen;
            int posFrom = Global.DTPAGE_DATA_POS + (size - 1) * recordLen;
            System.arraycopy(data,posFrom,data,posTo,recordLen);
        }
        decRecordSize(page);

        page.setDirty();
    }

    /**
     * 连着两个数据页
     */
    public static void connectPage(Page page1, Page page2) {
        setNextIndex(page1,page2.getPageId());
        setPreIndex(page2,page1.getPageId());
        page1.setDirty();
        page2.setDirty();
        page1.setDirty();
        page2.setDirty();
    }

    /**
     * 断开数据页的连接
     */
    public static void removeConnectPage(Page page) {
        int fileId = page.getFileId();
        try {
            int lastPageIndex = getPreIndex(page);
            int nextPageIndex = getNextIndex(page);
            if (lastPageIndex != -1) {
                Page lastPage = BufPageManager.getInstance().getPage(fileId,lastPageIndex);
                setNextIndex(lastPage,nextPageIndex);
            }
            if (nextPageIndex != -1) {
                Page nextPage = BufPageManager.getInstance().getPage(fileId,nextPageIndex);
                setPreIndex(nextPage,lastPageIndex);
            }
            page.setDirty();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据页是否存满记录
     */
    public static boolean isFull(Page page) {
        int size = getRecordSize(page);
        int cap = getCapacity(page);
        return size >= cap;
    }

    //------------------------获取页信息------------------------
    public static void setIndex(Page page, int index) { // 该页索引号
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DTPAGE_IDX_POS, index);
    }
    public static int getIndex(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_IDX_POS);
    }
    public static void setPreIndex(Page page, int index) { // 该页索引号
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DTPAGE_PREIDX_POS, index);
    }
    public static int getPreIndex(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_PREIDX_POS);
    }
    public static void setNextIndex(Page page, int index) { // 该页索引号
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DTPAGE_NEXTIDX_POS, index);
    }
    public static int getNextIndex(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_NEXTIDX_POS);
    }
    public static void setProp(Page page, int prop) { // 该页索引号
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DTPAGE_PROP_POS, prop);
    }
    public static int getProp(Page page) { // 该页索引号
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_PROP_POS);
    }
    public static void setRecordLen(Page page, int len) { // 该页索引号
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DTPAGE_RECORDLEN_POS,len);
    }
    public static int getRecordLen(Page page) { // 该页索引号
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_RECORDLEN_POS);
    }
    public static void setRecordSize(Page page, int size) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DTPAGE_SIZE_POS,size);
    }
    public static int getRecordSize(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_SIZE_POS);
    }
    public static void incRecordSize(Page page) {
        int size = getRecordSize(page);
        setRecordSize(page,++size);
    }
    public static void decRecordSize(Page page) {
        int size = getRecordSize(page);
        setRecordSize(page,--size);
    }
    public static void setCapacity(Page page, int cap) {
        ByteBuffer buffer = page.getBuffer();
        buffer.putInt(Global.DTPAGE_CAP_POS,cap);
    }
    public static int getCapacity(Page page) {
        ByteBuffer buffer = page.getBuffer();
        return buffer.getInt(Global.DTPAGE_CAP_POS);
    }
}
