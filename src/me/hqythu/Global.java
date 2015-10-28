package me.hqythu;

/**
 * Created by apple on 15/10/27.
 */
public class Global {
    /**
     * 页式文件系统
     */
    public static final int MAX_CACHE_SIZE = 65536;
    public static final int PAGE_BYTE_SIZE = 8192;
    public static final int PAGE_SIZE_IDX = 13; // 2^13 = 8192
    public static final int MAX_FILE_NUM = 128;

    /**
     * 页
     */
    public static final int PER_PAGE_INFO = 96;
    public static final int PER_PAGE_DATA = 8096;

    /**
     * 数据库文件的第一页
     */
    public static final int FIRST_PAGE_INFO_POS = 0;
    public static final int FIRST_PAGE_INFO_LEN = 256;
    public static final int FIRST_PAGE_BITMAP_POS = FIRST_PAGE_INFO_POS + FIRST_PAGE_INFO_LEN;
    public static final int FIRST_PAGE_BITMAP_LEN = 4096;
    public static final int FIRST_PAGE_TABLE_POS = FIRST_PAGE_BITMAP_POS + FIRST_PAGE_BITMAP_LEN;
    public static final int FIRST_PAGE_TABLE_LEN = (PER_PAGE_DATA - FIRST_PAGE_BITMAP_LEN - FIRST_PAGE_INFO_LEN);

    public static final int PER_TABLE_INFO_LEN = 128;
    public static final int TABLE_MAX_SIZE = FIRST_PAGE_TABLE_LEN / PER_TABLE_INFO_LEN;

    /**
     * 表页信息
     */
    public static final int PER_COL_INFO_POS = 128;
    public static final int PER_COL_INFO_LEN = 128;
}
