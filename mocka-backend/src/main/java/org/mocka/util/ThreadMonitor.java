package org.mocka.util;

import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;

public class ThreadMonitor {

    private final ThreadMXBean threadMXBean;
    private final long threadId;


    public ThreadMonitor(Thread thread) {
        this(thread.getId());
    }

    public ThreadMonitor(long threadId) {
        this.threadMXBean = getThreadMXBeanForThreadMonitor();
        this.threadMXBean.setThreadAllocatedMemoryEnabled(true);
        this.threadMXBean.setThreadCpuTimeEnabled(true);
        this.threadId = threadId;
    }


    private static ThreadMXBean getThreadMXBeanForThreadMonitor() {
        final var jvmThreadMXBean = ManagementFactory.getThreadMXBean();
        if (!(jvmThreadMXBean instanceof ThreadMXBean)) {
            throw new IllegalArgumentException(Formatter.format(
                "Cannot initialize ThreadMonitor. {} is not instance of {}",
                jvmThreadMXBean.getClass().getName(),
                ThreadMXBean.class.getName()));
        }
        return (ThreadMXBean) jvmThreadMXBean;
    }


    public long getThreadAllocatedBytes() {
        final long threadAllocatedBytes = threadMXBean.getThreadAllocatedBytes(threadId);
        checkValue(threadAllocatedBytes);
        return threadAllocatedBytes;
    }

    public long getThreadCpuTime() {
        final long threadCpuTime = threadMXBean.getThreadCpuTime(threadId);
        checkValue(threadCpuTime);
        return threadCpuTime;
    }

    private void checkValue(long value) {
        if (value == -1) {
            throw new IllegalStateException(Formatter.format(
                "Monitored thread {} is not alive or does not exist",
                threadId));
        }
    }
}
