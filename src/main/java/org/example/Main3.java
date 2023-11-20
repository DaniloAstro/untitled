import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.*;


public class Main3 {
    private static final int THREAD_COUNT = 5;
    private static AtomicInteger threadCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        // CountDownLatch example
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                System.out.println("Thread running");
                latch.countDown();
                threadCounter.incrementAndGet();
            });
        }

        // Зачекайте, поки всі потоки завершать використання CountDownLatch
        try {
            latch.await();
            System.out.println("All threads have finished using CountDownLatch");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Semaphore example
        Semaphore semaphore = new Semaphore(5); // Дозволити 2 потокам працювати одночасно
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("Thread running with Semaphore");
                    semaphore.release();
                    threadCounter.incrementAndGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // ReentrantLock example
        ReentrantLock lock = new ReentrantLock();
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                lock.lock();
                try {
                    System.out.println("Thread running with ReentrantLock");
                    threadCounter.incrementAndGet();
                } finally {
                    lock.unlock();
                }
            });
        }

        // CyclicBarrier example
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> System.out.println("All threads have reached the barrier"));
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    System.out.println("Thread running and waiting at barrier");
                    barrier.await();
                    threadCounter.incrementAndGet();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        // Shutdown the ThreadPool
        executorService.shutdown();

        // Перевірити, що станеться, якщо завдання відправлено після завершення роботи
        try {
            executorService.submit(() -> System.out.println("This task won't be executed"));
        } catch (RejectedExecutionException e) {
            System.out.println("Task submission after shutdown is rejected");
        }

        // Вивід кількості запущених потоків
        System.out.println("Загальна кількість виконаних потоків: " + threadCounter.get());
    }
}