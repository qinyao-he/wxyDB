package me.hqythu.wxydb.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectOption {
    public SelectOption() {
        func = null;
        tableNames = new ArrayList<>();
        columnNames = new ArrayList<>();
        fromTableNames = new HashSet<>();
    }
    public SelectOption(boolean all) {
        func = null;
        fromTableNames = new HashSet<>();
        if (all) {
            tableNames = null;
            columnNames = null;
        } else {
            tableNames = new ArrayList<>();
            columnNames = new ArrayList<>();
        }
    }
    public Func func;
    public List<String> tableNames;
    public List<String> columnNames;
    public Set<String> fromTableNames;

    public boolean isFunc() {return func != null;}
    public boolean isAll() {return tableNames == null;}
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
    public void setFunc(Func func) {
        this.func = func;
    }
    public String toString() {
        if (isAll()) {
            return "*";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("SET ");
            for (int i = 0; i < tableNames.size(); i++) {
                builder.append(tableNames.get(i));
                builder.append('.');
                builder.append(columnNames.get(i));
                if (i != tableNames.size() - 1) {
                    builder.append(',');
                }
            }
            return builder.toString();
        }
    }
}
