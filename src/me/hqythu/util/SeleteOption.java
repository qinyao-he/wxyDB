package me.hqythu.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeleteOption {
    public SeleteOption() {
        tableNames = new ArrayList<>();
        columnNames = new ArrayList<>();
        fromTableNames = new HashSet<>();
    }
    public List<String> tableNames;
    public List<String> columnNames;
    public Set<String> fromTableNames;
}
