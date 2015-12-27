package me.hqythu.wxydb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectOption {
    public SelectOption() {
        tableNames = new ArrayList<>();
        columnNames = new ArrayList<>();
        fromTableNames = new HashSet<>();
    }

    public List<String> tableNames;
    public List<String> columnNames;
    public Set<String> fromTableNames;

    public void add(String tableName, String columnName) {
        tableNames.add(tableName);
        fromTableNames.add(tableName);
        columnNames.add(columnName);
    }
}
