package org.mocka.runner.executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mocka.util.ThreadMonitor;

public class ResourceLimitedExecutor implements Executor {

    private final Executor executor = new ForkJoinPool();


    @Override
    public void execute(@NonNull Runnable task) {
        Executors
            .newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(() -> { }, 0, 100, TimeUnit.MILLISECONDS);

        Executors.newCachedThreadPool();

        final var job = new KillableJob(task);
        executor.execute(job);
        if (job.wasKilled()) {
            throw new RuntimeException(); // todo
        }
    }


    @RequiredArgsConstructor
    private static class KillableJob implements Runnable {

        private final CountDownLatch latch = new CountDownLatch(1);
        private final Runnable task;

        private boolean wasKilled = false;

        private ThreadMonitor monitor;
        private long allocatedBytesAtStart;
        private long cpuTimeAtStart;

        @Override
        public void run() {
            try {
                monitor = ThreadMonitor.forCurrentThread();
                allocatedBytesAtStart = monitor.getThreadAllocatedBytes();
                cpuTimeAtStart = monitor.getThreadCpuTime();
                task.run();
            } catch (ThreadDeath threadDeath) {
                wasKilled = true;
                throw threadDeath; // it's important to rethrow ThreadDeath
            } finally {
                latch.countDown();
            }
        }

        @SneakyThrows
        public boolean wasKilled() {
            latch.await();
            return wasKilled;
        }

        public long getAllocatedBytes() {
            return monitor.getThreadAllocatedBytes() - allocatedBytesAtStart;
        }

        public long getCpuTime() {
            return monitor.getThreadCpuTime() - cpuTimeAtStart;
        }
    }
}
