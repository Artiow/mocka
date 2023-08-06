package org.mocka.util;

import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import lombok.NonNull;

public class ThreadMonitor {

    private static final ThreadMXBean THREAD_MX_BEAN = getThreadMXBean();

    private final Thread threadToMonitor;


    public ThreadMonitor(@NonNull Thread threadToMonitor) {
        this.threadToMonitor = threadToMonitor;
    }


    public static ThreadMonitor forCurrentThread() {
        return new ThreadMonitor(Thread.currentThread());
    }

    private static long getThreadAllocatedBytes(long threadId) {
        return THREAD_MX_BEAN.getThreadAllocatedBytes(threadId);
    }

    private static long getThreadCpuTime(long threadId) {
        return THREAD_MX_BEAN.getThreadCpuTime(threadId);
    }

    private static ThreadMXBean getThreadMXBean() {
        final var jvmThreadMXBean = ManagementFactory.getThreadMXBean();
        if (!(jvmThreadMXBean instanceof ThreadMXBean)) {
            throw new UnsupportedOperationException(Formatter.format(
                "Cannot initialize ThreadMonitor. {} is not instance of {}",
                jvmThreadMXBean.getClass().getName(),
                ThreadMXBean.class.getName()));
        }

        final var threadMXBean = (ThreadMXBean) jvmThreadMXBean;
        if (!threadMXBean.isThreadAllocatedMemorySupported()) {
            throw new UnsupportedOperationException(Formatter.format(
                "Cannot initialize ThreadMonitor. {} does not support allocated memory measurement",
                threadMXBean.getClass().getName()));
        }
        if (!threadMXBean.isThreadCpuTimeSupported()) {
            throw new UnsupportedOperationException(Formatter.format(
                "Cannot initialize ThreadMonitor. {} does not support CPU time measurement",
                threadMXBean.getClass().getName()));
        }

        threadMXBean.setThreadAllocatedMemoryEnabled(true);
        threadMXBean.setThreadCpuTimeEnabled(true);
        return threadMXBean;
    }


    public long getThreadAllocatedBytes() {
        final long threadAllocatedBytes = getThreadAllocatedBytes(threadToMonitor.getId());
        checkValue(threadAllocatedBytes);
        return threadAllocatedBytes;
    }

    public long getThreadCpuTime() {
        final long threadCpuTime = getThreadCpuTime(threadToMonitor.getId());
        checkValue(threadCpuTime);
        return threadCpuTime;
    }


    private void checkValue(long value) {
        if (value == -1) {
            throw new IllegalStateException(Formatter.format(
                "Monitored thread {} is not alive or does not exist",
                threadToMonitor.getName()));
        }
    }
}
