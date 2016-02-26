package threadPool;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by esin on 06.12.2015.
 */
public class ThreadPoolMain {
    private static final int THREAD_COUNT = 4;
    private static final int TASK_COUNT = 100;

    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        //final ExecutorService executorService = Executors.newCachedThreadPool();
        //final ExecutorService executorService = Executors.newSingleThreadExecutor();
        System.out.println("Thread pool started");

        final Queue<Callable<Void>> taskQueue = new ArrayDeque<>(TASK_COUNT);
        for (int i = 0; i < TASK_COUNT; i++) {
            taskQueue.add(new Task(i));
        }

        System.out.println("Submitting tasks...");
        for (int i = 0; i < TASK_COUNT; i++) {
            final Future<Void> future = executorService.submit(taskQueue.poll());
        }
        System.out.println("All tasks are submitted!");

        System.out.println("Shutting down thread pool");
        executorService.shutdown();

//        System.out.println("Shutting down thread pool NOW!");
//        final List<Runnable> remainingTasks = executorService.shutdownNow();
//        System.out.println("Tasks not executed: " + remainingTasks.size());
    }

    private static final class Task implements Callable<Void> {
        private final int taskNumber;

        private Task(int taskNumber) {
            this.taskNumber = taskNumber;
        }

        @Override
        public Void call() {
            System.out.println(Thread.currentThread().getName() + " completed task " + taskNumber);
            return null;
        }
    }
}
