package org.mocka.util;

import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class MeasuredConcurrentLatchRunner extends ConcurrentLatchRunner {

    private final Supplier<Stopwatch> stopwatchProvider;
    private Collection<Long> data;


    public MeasuredConcurrentLatchRunner(Supplier<Stopwatch> stopwatchProvider) {
        this.stopwatchProvider = stopwatchProvider;
    }


    public void addMeasuredTest(String threadName, ThrowingConsumer<LocalStopwatch> test) {
        addTest(threadName, () -> {
            final var sw = new LocalStopwatch(stopwatchProvider.get());
            test.accept(sw);
            data.add(sw.elapsedNanos());
        });
    }


    public Stats calculateStats() {
        return Stats.calc(getData());
    }

    public Collection<Long> getData() {
        if (!isDone()) {
            throw new IllegalStateException("Latch is not done yet");
        }

        return Collections.unmodifiableCollection(data);
    }


    @Override
    public void releaseLatch() {
        try {
            data = Collections.synchronizedCollection(new ArrayList<>(getTestCount()));
            super.releaseLatch();
        } catch (Exception e) {
            data = null;
            throw e;
        }
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocalStopwatch {

        private final Stopwatch stopwatch;

        public void start() {
            stopwatch.start();
        }

        public void stop() {
            stopwatch.stop();
        }

        private long elapsedNanos() {
            if (stopwatch.isRunning()) {
                stopwatch.stop();
            }

            return stopwatch.elapsed(TimeUnit.NANOSECONDS);
        }
    }


    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Stats {

        private final double mean;
        private final double median;
        private final long max;
        private final long min;


        private static Stats calc(Collection<Long> data) {
            var sortedData = data.stream().mapToLong(Long::valueOf).sorted().toArray();

            final int dataLength = sortedData.length;
            final int dataMidIndex = dataLength / 2;

            @SuppressWarnings("OptionalGetWithoutIsPresent")
            double mean = LongStream.of(sortedData).average().getAsDouble();

            double median = (dataLength % 2 == 0)
                ? (sortedData[dataMidIndex - 1] + sortedData[dataMidIndex]) / 2.0d
                : sortedData[dataMidIndex];

            return new Stats(mean, median, sortedData[dataLength - 1], sortedData[0]);
        }
    }
}
