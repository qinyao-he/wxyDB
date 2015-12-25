package me.hqythu.util;

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
    public static final int PER_PAGE_DATA = 8096;

    /**
     * 库页（数据库文件的第一页）
     */
    public static final int DBPAGE_INFO_POS = 0;
    public static final int DBPATE_INFO_TABLESIZE = 0;
    public static final int DBPAGE_BITMAP_POS = 256;
    public static final int DBPAGE_INFO_LEN = DBPAGE_BITMAP_POS - DBPAGE_INFO_POS;
    public static final int DBPAGE_BITMAP_LEN = 4096;
    public static final int DBPAGE_TABLE_POS = DBPAGE_BITMAP_POS + DBPAGE_BITMAP_LEN;
    public static final int DBPAGE_TABLE_LEN = (PER_PAGE_DATA - DBPAGE_BITMAP_LEN - DBPAGE_INFO_LEN);

    public static final int PER_TABLE_INFO_LEN = 128;
    public static final int TABLE_MAX_SIZE = DBPAGE_TABLE_LEN / PER_TABLE_INFO_LEN;

    /**
     * 表页信息
     */
    public static final int TBPAGE_NAME_POS = 0;
    public static final int TBPAGE_PRIMARY_POS = 100;
    public static final int TBPAGE_PROP_POS = 104;
    public static final int TBPAGE_PAGEIDX_POS = 108;
    public static final int TBPAGE_DATAIDX_POS = 112;
    public static final int TBPAGE_RECORD_SIZE_POS = 116;
    public static final int TBPAGE_RECORD_LEN_POS = 120;
    public static final int TBPAGE_COLUMN_POS = 124; // 2
    public static final int COL_NAME_POS = 0;
    public static final int COL_PROP_POS = 120;
    public static final int COL_TYPE_POS = 124;
    public static final int COL_LEN_POS = 126;

    public static final int TABLE_NAME_LEN = TBPAGE_PRIMARY_POS - TBPAGE_NAME_POS; // 100
    public static final int COL_NAME_LEN = COL_PROP_POS - COL_NAME_POS; // 120
    public static final int COL_INFO_POS = 128;
    public static final int PER_COL_INFO_LEN = 128;

    /**
     * 数据页
     */
    public static final int DTPAGE_INFO_POS = 0;
    public static final int DTPAGE_IDX_POS = 0;
    public static final int DTPAGE_PREIDX_POS = 4;
    public static final int DTPAGE_PROP_POS = 8;
    public static final int DTPAGE_RECORDLEN_POS = 12;
    public static final int DTPAGE_NEXTIDX_POS = 16;
    public static final int DTPAGE_SIZE_POS = 20;
    public static final int DTPAGE_CAP_POS = 24;
    public static final int DTPAGE_DATA_POS = 96;

    public static final int DTPAGE_INFO_LEN = DTPAGE_DATA_POS - DTPAGE_INFO_POS;
    public static final int DTPAGE_DATA_LEN = PAGE_BYTE_SIZE - DTPAGE_DATA_POS;
    /**
     * 行结构
     */
    public static final int RECORD_STATUSA_POS = 0;
    public static final int RECORD_STATUSB_POS = 1;
    public static final int RECORD_STATIC_LEN_POS = 2;
    public static final int RECORD_COLNUM_POS = 4;
    public static final int RECORD_STATIC_DATA_POS = 6;
    public static final int RECORD_STATUS_LEN = RECORD_STATIC_DATA_POS - RECORD_STATUSA_POS;
}
