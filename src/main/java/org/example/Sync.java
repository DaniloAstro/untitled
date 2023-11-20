package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class Sync {

    public static void main(String[] args) throws InterruptedException {
        var container = new CounterContainer();
        AtomicInteger atomicInteger = new AtomicInteger();

        var thread1 = new Thread(() -> {
            for (int i = 0; i < 100_100; i++) {
                container.rawInt = container.rawInt + 1;
                container.increment();
                atomicInteger.incrementAndGet();
            }
        });

        var thread2 = new Thread(() -> {
            for (int i = 0; i < 100_100; i++) {
                container.rawInt = container.rawInt + 1;
                container.increment();
                atomicInteger.incrementAndGet();
            }
        });

        thread1.start();
        thread2.start();
        // Зачекайте на завершення потоків
        thread1.join();
        thread2.join();

        // Доступ до лічильника після завершення потоків
        System.out.println("Counter value: " + container.getCounter());
        System.out.println("AtomicInteger value: " + atomicInteger.get());
        System.out.println("Raw int value: " + container.rawInt);
    }

    static class CounterContainer {
        private int counter = 0;
        private int rawInt = 0;

        public synchronized void increment() {
            counter = counter + 1;
        }

        public int getCounter() {
            return counter;
        }
    }
}
