package org.mocka.util;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Stopwatches {

    public static Stopwatch cpu() {
        return Stopwatch.createUnstarted(new CpuTicker());
    }

    public static Stopwatch wall() {
        return Stopwatch.createUnstarted(new WallTicker());
    }


    private static class CpuTicker extends Ticker {

        private final ThreadMonitor threadMonitor;
        private final Thread holderThread;


        public CpuTicker() {
            final var currentThread = Thread.currentThread();
            this.threadMonitor = new ThreadMonitor(currentThread);
            this.holderThread = currentThread;
        }


        @Override
        public long read() {
            checkThread();
            return threadMonitor.getThreadCpuTime();
        }

        private void checkThread() {
            var currentThread = Thread.currentThread();
            if (!Objects.equals(holderThread, currentThread)) {
                var holderThreadName = holderThread.getName();
                var currentThreadName = currentThread.getName();
                throw new IllegalStateException(Formatter.format(
                    "CPU stopwatch can only be used within a single thread. Stopwatch holder thread: {}, current thread: {}",
                    holderThreadName,
                    currentThreadName));
            }
        }
    }

    private static class WallTicker extends Ticker {

        @Override
        public long read() {
            return System.nanoTime();
        }
    }
}
