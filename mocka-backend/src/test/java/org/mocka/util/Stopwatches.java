package org.mocka.util;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
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

        private final ThreadMXBean threadMXBean;
        private final Thread holderThread;

        public CpuTicker() {
            this.threadMXBean = ManagementFactory.getThreadMXBean();
            this.holderThread = Thread.currentThread();
        }

        @Override
        public long read() {
            checkThread();
            final long cpuTime = threadMXBean.getThreadCpuTime(holderThread.getId());
            if (cpuTime == -1) {
                throw new IllegalStateException(Formatter.format(
                    "Current thread {} is not alive",
                    holderThread.getName()
                ));
            }
            return cpuTime;
        }

        private void checkThread() {
            var currentThread = Thread.currentThread();
            if (!Objects.equals(holderThread, currentThread)) {
                var holderThreadName = holderThread.getName();
                var currentThreadName = currentThread.getName();
                throw new IllegalStateException(Formatter.format(
                    "CPU stopwatch can only be used within a single thread. Stopwatch holder thread: {}, current thread: {}",
                    holderThreadName,
                    currentThreadName
                ));
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
