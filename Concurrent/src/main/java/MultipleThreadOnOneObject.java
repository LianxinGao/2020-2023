package main.java;

import sun.misc.MessageUtils;

import java.util.concurrent.locks.ReentrantLock;

// 买火车票为例子, 多个线程操作同一份资源。
public class MultipleThreadOnOneObject implements Runnable{
    private int ticketNumber = 10;
    private ReentrantLock lock = new ReentrantLock();
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                lock.lock();
                if (ticketNumber > 0){
                    System.out.println(Thread.currentThread().getName() + " get the " +ticketNumber--+ " ticket!");
                }else break;
            }finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        MultipleThreadOnOneObject tickets = new MultipleThreadOnOneObject();
        new Thread(tickets, "Liam").start();
        new Thread(tickets, "Avril").start();
        new Thread(tickets, "Scalper").start();
    }
}
