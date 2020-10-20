[文章出处](https://blog.csdn.net/guxianyang/article/details/78241384)  
Java中HashMap内部有个`Entry[] table`存放数据。  
Entry类的定义大致是：  
```
class Entry{
    Object key
    Object value
    Entry next;
}
```
`Entry[] table`中每个元素都是一个链表，即HashMap内部存储使用**数组+链表**的方式，即拉链式存储。  
- - -
存储：put(key, value)
1. key.hashCode() & (table.length() - 1) 作为index， 新的Entry就属于table[index]这个链表，
若链表不存在，则作为链表的头部。
2. 然后从前往后遍历table[index]这个链表，若key.equals(entry.key)则替换旧值，否则新的Entry插入table[index]的最前面。
