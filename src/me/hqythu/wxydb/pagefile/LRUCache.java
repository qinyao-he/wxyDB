package me.hqythu.wxydb.pagefile;

import java.util.Hashtable;

/**
 * LRUCache
 * Least Recently Used 近期最少使用算法
 * 用于缓存
 */
public class LRUCache {
    class CacheNode {
        CacheNode prev = null;
        CacheNode next = null;
        Long key = null;
        Page value = null;

        CacheNode() {
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.nodes = new Hashtable<>(capacity);
    }

    public void put(Long key, Page value) {
        CacheNode node = nodes.get(key);
        if (node == null) {
            if (nodes.size() >= capacity) {
                if (last != null) { // 删除最少使用
                    remove(last.key);
                }
            }
            node = new CacheNode();
        }
        node.key = key;
        node.value = value;
        moveToFirst(node);
        nodes.put(key, node);
    }

    public Page get(Long key) {
        CacheNode node = nodes.get(key);
        if (node != null) {
            moveToFirst(node);
            return node.value;
        } else {
            return null;
        }
    }

    /**
     * 删除
     * 从缓存区中删除
     */
    public void remove(Long key) {
        CacheNode node = nodes.remove(key);
        if (node != null) {
            link(node.prev, node.next);
            if (last == node)
                last = node.prev;
            if (first == node)
                first = node.next;
            node.value.writeBack();
        }
    }

    /**
     * 清空缓存
     */
    public void clear() {
        for (CacheNode node : nodes.values()) {
            node.value.writeBack();
        }
        nodes.clear();
        first = null;
        last = null;
    }

    private void link(CacheNode prev, CacheNode next) {
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
    }

    private void moveToFirst(CacheNode node) {
        link(node.prev, node.next);
        if (first == null) {
            first = node;
        } else if (node != first) {
            link(node, first);
            first = node;
        }
        link(null, node);
        if (last == null) {
            last = first;
        }
    }

    private int capacity;
    private Hashtable<Long, CacheNode> nodes; //缓存容器
    private CacheNode first; //链表头
    private CacheNode last; //链表尾

    public static void main(String[] args) {
    }
}
