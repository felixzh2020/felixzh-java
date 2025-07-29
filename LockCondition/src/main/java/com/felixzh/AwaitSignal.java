package com.felixzh;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AwaitSignal {
    ExecutorService executor;
    private final ReentrantLock lock;
    private final Condition coffeeCondition;
    private boolean coffeePrepared;

    private AwaitSignal() {
        executor = new ThreadPoolExecutor(
                2,
                4,
                10000,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        lock = new ReentrantLock();
        coffeeCondition = lock.newCondition();
        coffeePrepared = false;
    }

    private static class SingletonHolder {
        private static final AwaitSignal instance = new AwaitSignal();
    }

    public static AwaitSignal getInstance() {
        return SingletonHolder.instance;
    }

    private static String formatTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date(time));
    }

    private void orderCoffee() {
        long startTime = System.currentTimeMillis();
        System.out.println("顾客开始下单时间: " + formatTime(startTime));
        lock.lock();
        try {
            System.out.println("顾客：下单并付款，等待咖啡准备...");
            while (!coffeePrepared) {
                coffeeCondition.await(); // 顾客线程等待咖啡准备好
            }
            long getCoffeeTime = System.currentTimeMillis();
            System.out.println("顾客取到咖啡的时间: " + formatTime(getCoffeeTime));
            // 顾客拿到咖啡
            System.out.println("顾客：取到咖啡，离开咖啡店");
            coffeePrepared = false; // 准备下一单
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("顾客线程被中断");
        } finally {
            lock.unlock();
        }
    }

    private void makeCoffee() {
        lock.lock();
        try {
            long getOrderTime = System.currentTimeMillis();
            System.out.println("老板接到订单的时间: " + formatTime(getOrderTime));
            // 老板准备咖啡
            System.out.println("老板：开始准备咖啡...");
            // 模拟制备咖啡的时间
            TimeUnit.MILLISECONDS.sleep(2000);
            coffeePrepared = true;
            coffeeCondition.signal(); // 通知顾客咖啡准备好了
            System.out.println("老板：咖啡准备好了，通知顾客取走");
            long notifyTime = System.currentTimeMillis();
            System.out.println("老板通知顾客线程的时间: " + formatTime(notifyTime));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("老板线程被中断");
        } finally {
            lock.unlock();
        }
    }

    public void buyCoffee() throws Exception{
        executor.submit(this::orderCoffee);
        Thread.sleep(1000);
        executor.submit(this::makeCoffee);

        executor.shutdown();
        try {
            // 等待所有任务完成
            if (!executor.awaitTermination(8, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
