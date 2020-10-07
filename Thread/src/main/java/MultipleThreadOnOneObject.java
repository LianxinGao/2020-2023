package main.java;

import sun.misc.MessageUtils;

// 买火车票为例子, 多个线程操作同一份资源。
public class MultipleThreadOnOneObject implements Runnable{
    private int ticketNumber = 10;

    public void run() {
        while (ticketNumber > 0){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " get the " +ticketNumber--+ " ticket!");
        }
    }

    public static void main(String[] args) {
        MultipleThreadOnOneObject tickets = new MultipleThreadOnOneObject();
        new Thread(tickets, "Liam").start();
        new Thread(tickets, "Avril").start();
        new Thread(tickets, "Scalper").start();
    }
}
