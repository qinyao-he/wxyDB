package me.hqythu.PageFile;

import me.hqythu.Global;
import me.hqythu.system.Table;
import me.hqythu.util.Column;
import me.hqythu.util.DataType;

import java.nio.ByteBuffer;

public class TablePageUser {

    private TablePageUser(){}

    /**
     * 初始化表首页
     */
    public static void initTablePage(Page page, String tableName, Column[] columns) {
        ByteBuffer buffer = page.getBuffer();
        buffer.position(0);
        buffer.put(tableName.getBytes()); // 表名
        buffer.putInt(Global.TABLE_COLNUM_POS, columns.length); // 列数
        for (int i = 0; i < columns.length; i++) {
            int pos = Global.PER_COL_INFO_POS + i * Global.PER_COL_INFO_LEN;
            buffer.position(pos);
            buffer.put(columns[i].name.getBytes());                              // 列名称 112, 未检查
            buffer.put((byte) 0);
            buffer.putInt(pos + Global.COL_PROP_POS, columns[i].prop);           // 数据属性
            buffer.putInt(pos + Global.COL_TYPE_POS, columns[i].type.ordinal()); // 数据类型 4
            buffer.putInt(pos + Global.COL_LEN_POS, columns[i].len);             // 数据长度 4
        }
        page.setDirty();
    }

    /**
     * 读取表首页，得到Table
     */
    public static Table readTablePage(Page page) {
        byte[] data = page.getData();
        ByteBuffer buffer = page.getBuffer();

        // 表信息
        String tableName = new String(data, Global.TABLE_NAME_POS, Global.TABLE_NAME_LEN);  // 表名
        int recordLen = buffer.getInt(Global.TABLE_RECORDLEN_POS);      // 每条记录长度
        int nRecord = buffer.getInt(Global.TABLE_RECORDNUM_POS);  // 记录个数
        int n = buffer.getInt(Global.TABLE_COLNUM_POS);           // 列数

        // 列信息
        Column[] cols = new Column[n];
        for (int i = 0; i < n; i++) {
            int offset = Global.PER_COL_INFO_POS + i * Global.PER_COL_INFO_LEN;

            String name = new String(data, offset + Global.COL_NAME_POS, Global.COL_NAME_LEN); // 列名
            int prop = buffer.getInt(offset + Global.COL_PROP_POS);                            // 列属性
            DataType type = DataType.valueOf(buffer.getInt(offset + Global.COL_TYPE_POS)); // 数据类型
            int len = buffer.getInt(offset + Global.COL_LEN_POS);                          // 数据列长

            cols[i] = new Column(name, prop, type, len);
        }
        return new Table(tableName,recordLen,nRecord,cols);
    }
}
