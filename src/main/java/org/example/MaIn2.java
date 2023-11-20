package org.example;

public class MaIn2 {
    public static void main(String[] args) {
        Runnable myTask = new MyTask();
        Runnable anotherTask = new AnotherTask();

        Thread task = new Thread(myTask);
        Thread task2 = new Thread(anotherTask);
        Thread task3 = new Thread(new ImplRunnableTask());
        Thread task4 = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


                for (int i = 0; i < 10; i++) {
                    System.err.println("task4 Hello, world from ImplRunnableTaskLambda Thread" + i);
            }
        });


        task.start();
        task2.start();
        task3.start();
        task4.start();


        return;
    }

    static class ImplRunnableTask implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.err.println("Hello, world from ImplRunnableTask Thread" + i);
        }
    }
}
    static class MyTask implements Runnable {
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.err.println("Hello, world from MyTask Thread" + i);
            }
        }
    }

    static class AnotherTask implements Runnable {
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.err.println("Hello, world from AnotherTask Thread" + i);
            }
        }
    }

}