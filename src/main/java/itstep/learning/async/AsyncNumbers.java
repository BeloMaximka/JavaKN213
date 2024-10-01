package itstep.learning.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AsyncNumbers {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public void run() {
        StringBuilder sb = new StringBuilder();
        Object lock = new Object();
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            tasks.add(new Rate(lock, sb, i));
        }
        CompletableFuture<?>[] futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(task, threadPool))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        threadPool.shutdown();
        System.out.println("Completed. Result: " + sb);
    }
}

class Rate implements  Runnable
{
    private final Object lock;
    private final StringBuilder numberString;
    private final int number;
    public Rate(Object lock, StringBuilder numberString, int number) {
        this.lock = lock;
        this.numberString = numberString;
        this.number = number ;
    }
    @Override
    public void run() {
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        synchronized (this.lock) {
            numberString.append(this.number);
            System.out.printf("Added %d: %s\n", this.number, this.numberString);
        }
    }
}