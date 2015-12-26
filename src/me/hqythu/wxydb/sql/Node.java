package me.hqythu.wxydb.sql;

public class Node {
    public enum Type {
        COND,
        SEP
    }

    Condition condition;
    String sep;
    Node leftChild;
    Node rightChild;
    Type type;
}
