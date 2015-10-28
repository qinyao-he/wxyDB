package me.hqythu.system;

import me.hqythu.record.Page;

/**
 * Created by apple on 15/10/28.
 */
public class Table {

    enum DataType {
        INT, VARCHAR
    }

    String name;
    String[] fileNames;
    DataType[] dataTypes;
    int[] lens;

    public Table() {

    }
    public Table(Page page) {

    }

    public String getName() {
        return name;
    }

}
