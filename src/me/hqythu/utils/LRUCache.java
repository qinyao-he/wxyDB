package me.hqythu.utils;

import java.util.Hashtable;

/**
 * LRUCache
 * Least Recently Used 近期最少使用算法
 * 用于缓存
 */
public class LRUCache<K, V> {
    class CacheNode<K, V> {
        CacheNode prev = null;
        CacheNode next = null;
        K key = null;
        V value = null;

        CacheNode() {
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.nodes = new Hashtable<>(capacity);
    }

    public void put(K key, V value) {
        CacheNode<K, V> node = nodes.get(key);
        if (node == null) {
            if (size >= capacity) {
                if (last != null) {// 删除最少使用
                    nodes.remove(last.key);
                    removeLast();
                }
            } else {
                size++;
            }
            node = new CacheNode<K, V>();
        }
        node.key = key;
        node.value = value;
        moveToFirst(node);
        nodes.put(key, node);
    }

    public V get(K key) {
        CacheNode<K, V> node = nodes.get(key);
        if (node != null) {
            moveToFirst(node);
            return node.value;
        } else {
            return null;
        }
    }

    /**
     * 删除
     * 缓存区中不真的删除
     *
     * @param key
     * @return
     */
    public V remove(K key) {
        CacheNode<K, V> node = nodes.get(key);
        if (node != null) {
            size--;
            link(node.prev, node.next);
            if (last == node)
                last = node.prev;
            if (first == node)
                first = node.next;
            return node.value;
        } else {
            return null;
        }
    }

    /**
     * 清空缓存
     */
    public void clear() {
        nodes.clear();
        first = null;
        last = null;
    }

    private void link(CacheNode<K, V> prev, CacheNode<K, V> next) {
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
    }


    private void removeLast() {
        if (last != null) {
            link(last.prev, null);
            last = last.prev;
        }
    }

    private void moveToFirst(CacheNode<K, V> node) {
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
    private Hashtable<K, CacheNode<K, V>> nodes; //缓存容器
    private int size;
    private CacheNode first; //链表头
    private CacheNode last; //链表尾

    public static void main(String[] args) {
//        LRUCache<Integer, String> lruCache = new LRUCache<>(400);
//        for (int i = 0; i < 500; i++) {
//            lruCache.put(i, String.format("%d", i));
//        }
//        for (int i = 0; i < 500; i++) {
//            System.out.print(lruCache.get(i) + " ");
//        }
    }
}
