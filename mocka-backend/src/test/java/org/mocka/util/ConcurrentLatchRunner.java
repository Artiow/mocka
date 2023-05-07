package org.mocka.util;

import java.util.concurrent.CountDownLatch;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;

public class ConcurrentLatchRunner implements ThrowingRunnable {

    private final CountDownLatch startLatch;

    private CountDownLatch doneLatch;
    @Getter(AccessLevel.PROTECTED)
    private int testCount;
    @Getter(AccessLevel.PROTECTED)
    private boolean released;
    @Getter(AccessLevel.PROTECTED)
    private boolean done;


    public ConcurrentLatchRunner() {
        startLatch = new CountDownLatch(1);
    }


    public void addTest(String threadName, ThrowingRunnable test) {
        new Thread(() -> awaitAndRun(test), threadName).start();
        testCount++;
    }


    @Override
    public void run() throws InterruptedException {
        releaseLatch();
        await();
    }

    public void releaseLatch() {
        if (released) {
            throw new IllegalStateException("Latch is already released");
        }
        if (done) {
            throw new IllegalStateException("Latch is already done");
        }

        doneLatch = new CountDownLatch(testCount);
        startLatch.countDown();
        released = true;
    }

    public void await() throws InterruptedException {
        if (!released) {
            throw new IllegalStateException("Latch is not released");
        }
        if (done) {
            throw new IllegalStateException("Latch is already done");
        }

        doneLatch.await();
        done = true;
    }


    @SneakyThrows
    private void awaitAndRun(ThrowingRunnable test) {
        startLatch.await();
        test.run();
        doneLatch.countDown();
    }
}
