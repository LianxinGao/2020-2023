[CAS](https://zhuanlan.zhihu.com/p/104179990)  
[AQS](https://zhuanlan.zhihu.com/p/89632622)
1. CAS(Compare and Swap)，是一种**无锁算法**。
    1. CAS有三个操作数：内存值V，旧的预期值O，要修改的新值N。
    2. 当且仅当O和V相同时，才将V修改为N，否则什么都不做。
2. 缺点
    1. ABA问题，先修改为B再修改为A，看起来值没变还是A，其实发生了变化。解决方案：加上版本号。
    