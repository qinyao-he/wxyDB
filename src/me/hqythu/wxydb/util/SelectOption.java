package me.hqythu.wxydb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectOption {
    public SelectOption() {
        this.all = false;
        tableNames = new ArrayList<>();
        columnNames = new ArrayList<>();
        fromTableNames = new HashSet<>();
    }
    public SelectOption(boolean all) {
        fromTableNames = new HashSet<>();
        if (all) {
            this.all = true;
        } else {
            this.all = false;
            tableNames = new ArrayList<>();
            columnNames = new ArrayList<>();
        }
    }
    public boolean all;
    public List<String> tableNames;
    public List<String> columnNames;
    public Set<String> fromTableNames;

    public void setAll() {all = true;}
    public void clear() {all = false;}
    public boolean isAll() {return all;}
    public Set<String> getFromTableNames() {
        return fromTableNames;
    }
    public void addFromTable(String tableName) {
        fromTableNames.add(tableName);
    }
    public void add(String tableName, String columnName) {
        tableNames.add(tableName);
        columnNames.add(columnName);
    }
}
