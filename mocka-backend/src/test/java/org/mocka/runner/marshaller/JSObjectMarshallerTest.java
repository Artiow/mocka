package org.mocka.runner.marshaller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mocka.util.RandomUtils.nextBoolean;
import static org.mocka.util.RandomUtils.nextBooleanArray;
import static org.mocka.util.RandomUtils.nextDouble;
import static org.mocka.util.RandomUtils.nextDoubleArray;
import static org.mocka.util.RandomUtils.nextHexString;
import static org.mocka.util.RandomUtils.nextInt;
import static org.mocka.util.RandomUtils.nextIntArray;
import static org.mocka.util.RandomUtils.nextLong;
import static org.mocka.util.RandomUtils.nextLongArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mocka.configuration.ScriptEngineConfiguration;
import org.mocka.properties.ScriptEngineProperties;
import org.mocka.util.Formatter;
import org.mocka.util.MeasuredConcurrentLatchRunner;
import org.mocka.util.Stopwatches;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ClassUtils;

@Slf4j
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(classes = {ObjectMapper.class, ScriptEngineConfiguration.class})
@EnableConfigurationProperties(value = {ScriptEngineProperties.class})
public class JSObjectMarshallerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ScriptEngine scriptEngine;

    private JSObjectMarshaller marshaller;


    @BeforeEach
    public void setup() throws ScriptException {
        marshaller = new JSObjectMarshaller(objectMapper, scriptEngine);
    }


    @Test
    @Order(0)
    public void warmUp() throws Exception {
        doMultithreadingTest(1, () -> generateTestPojo(1));
        doMultithreadingTest(1, () -> generateTestArray(1));
        doMultithreadingTest(1, () -> generateTestCollection(1));
        doMultithreadingTest(1, () -> generateTestMap(1));
    }


    @Test
    @Order(1)
    public void test_pojo() throws Exception {
        doTest(() -> generateTestPojo(4));
    }

    @Test
    @Order(2)
    public void test_array() throws Exception {
        doTest(() -> generateTestArray(4));
    }

    @Test
    @Order(3)
    public void test_collection() throws Exception {
        doTest(() -> generateTestCollection(4));
    }

    @Test
    @Order(4)
    public void test_map() throws Exception {
        doTest(() -> generateTestMap(4));
    }

    @Test
    @Order(5)
    public void multithreadingTest_pojo() throws Exception {
        doMultithreadingTest(1000, () -> generateTestPojo(2));
    }

    @Test
    @Order(6)
    public void multithreadingTest_array() throws Exception {
        doMultithreadingTest(1000, () -> generateTestArray(2));
    }

    @Test
    @Order(7)
    public void multithreadingTest_collection() throws Exception {
        doMultithreadingTest(1000, () -> generateTestCollection(2));
    }

    @Test
    @Order(8)
    public void multithreadingTest_map() throws Exception {
        doMultithreadingTest(1000, () -> generateTestMap(2));
    }


    private void doTest(
        Supplier<Object> sourceGenerator
    ) throws Exception {
        var source = sourceGenerator.get();
        var result = marshaller.marshall(source);
        doAssertEquals(source, result);
    }

    private void doMultithreadingTest(
        int numberOfIterations,
        Supplier<Object> sourceGenerator
    ) throws Exception {
        doMultithreadingTest(numberOfIterations, sourceGenerator, Stopwatches::wall);
    }

    private void doMultithreadingTest(
        int numberOfIterations,
        Supplier<Object> sourceGenerator,
        Supplier<Stopwatch> stopwatchProvider
    ) throws Exception {
        final var latchRunner = new MeasuredConcurrentLatchRunner(stopwatchProvider);
        final Map<Integer, Object> sourceMap = new HashMap<>(numberOfIterations);
        final Map<Integer, Object> resultMap = new ConcurrentHashMap<>(numberOfIterations);
        for (int i = 0; i < numberOfIterations; i++) {
            final var key = i;
            final var source = sourceGenerator.get();
            sourceMap.put(key, source);
            latchRunner.addMeasuredTest("TestThread-" + key, sw -> {
                sw.start();
                final var result = marshaller.marshall(source);
                sw.stop();
                resultMap.put(key, result);
            });
        }

        final var sw = stopwatchProvider.get();
        sw.start();
        latchRunner.run();
        sw.stop();

        final var stats = latchRunner.calculateStats();
        final double msFactor = 1 / 1000000.0d;
        final var formatter = new DecimalFormat("#0.000", DecimalFormatSymbols.getInstance(Locale.US));
        log.info(
            "Stats:\n|total:  {}ms\n|mean:   {}ms\n|median: {}ms\n|max:    {}ms\n|min:    {}ms",
            formatter.format(msFactor * sw.elapsed(TimeUnit.NANOSECONDS)),
            formatter.format(msFactor * stats.getMean()),
            formatter.format(msFactor * stats.getMedian()),
            formatter.format(msFactor * stats.getMax()),
            formatter.format(msFactor * stats.getMin())
        );

        var missingKeys = Sets.difference(sourceMap.keySet(), resultMap.keySet());
        assertTrue(missingKeys.isEmpty(), Formatter.format("Missing keys found: {}", missingKeys));
        assertEquals(numberOfIterations, sourceMap.size());
        assertEquals(numberOfIterations, resultMap.size());
        for (int i = 0; i < numberOfIterations; i++) {
            var source = sourceMap.get(i);
            var result = resultMap.get(i);
            doAssertEquals(source, result);
        }
    }


    private void doAssertEquals(Object expected, Object actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            final var expectedClass = expected.getClass();
            if (ClassUtils.isPrimitiveWrapper(expectedClass) || expected instanceof String) {
                assertEquals(expected, actual);
            } else if (expectedClass.isArray()) {
                if (expected instanceof boolean[]) {
                    doAssertArrayEquals((boolean[]) expected, actual);
                } else if (expected instanceof int[]) {
                    doAssertArrayEquals((int[]) expected, actual);
                } else if (expected instanceof long[]) {
                    doAssertArrayEquals((long[]) expected, actual);
                } else if (expected instanceof double[]) {
                    doAssertArrayEquals((double[]) expected, actual);
                } else {
                    doAssertArrayEquals((Object[]) expected, actual);
                }
            } else if (expected instanceof Collection) {
                // noinspection unchecked
                doAssertArrayEquals((Collection<Object>) expected, actual);
            } else if (expected instanceof Map) {
                // noinspection unchecked
                doAssertMapEquals((Map<String, Object>) expected, actual);
            } else if (expected instanceof TestPojo) {
                doAssertEquals((TestPojo) expected, actual);
            } else {
                fail(Formatter.format("Unexpected {}", expectedClass.getName()));
            }
        }
    }

    private void doAssertArrayEquals(boolean[] expected, Object actual) {
        final var actualJSObject = doAssertIsArray(actual);
        assertEquals(expected.length, actualJSObject.keySet().size());
        for (int i = 0; i < expected.length; i++) {
            // noinspection UnnecessaryBoxing
            assertEquals(Boolean.valueOf(expected[i]), actualJSObject.getSlot(i), "index: " + i);
        }
    }

    private void doAssertArrayEquals(int[] expected, Object actual) {
        final var actualJSObject = doAssertIsArray(actual);
        assertEquals(expected.length, actualJSObject.keySet().size());
        for (int i = 0; i < expected.length; i++) {
            // noinspection UnnecessaryBoxing
            assertEquals(Integer.valueOf(expected[i]), actualJSObject.getSlot(i), "index: " + i);
        }
    }

    private void doAssertArrayEquals(long[] expected, Object actual) {
        final var actualJSObject = doAssertIsArray(actual);
        assertEquals(expected.length, actualJSObject.keySet().size());
        for (int i = 0; i < expected.length; i++) {
            // noinspection UnnecessaryBoxing
            assertEquals(Long.valueOf(expected[i]), actualJSObject.getSlot(i), "index: " + i);
        }
    }

    private void doAssertArrayEquals(double[] expected, Object actual) {
        final var actualJSObject = doAssertIsArray(actual);
        assertEquals(expected.length, actualJSObject.keySet().size());
        for (int i = 0; i < expected.length; i++) {
            // noinspection UnnecessaryBoxing
            assertEquals(Double.valueOf(expected[i]), actualJSObject.getSlot(i), "index: " + i);
        }
    }

    private void doAssertArrayEquals(Object[] expected, Object actual) {
        final var actualJSObject = doAssertIsArray(actual);
        assertEquals(expected.length, actualJSObject.keySet().size());
        for (int i = 0; i < expected.length; i++) {
            doAssertEquals(expected[i], actualJSObject.getSlot(i));
        }
    }

    private void doAssertArrayEquals(Collection<Object> expected, Object actual) {
        final var actualJSObject = doAssertIsArray(actual);
        assertEquals(expected.size(), actualJSObject.keySet().size());
        int i = 0; for (Object expectedElement : expected) {
            doAssertEquals(expectedElement, actualJSObject.getSlot(i++));
        }
    }

    private void doAssertMapEquals(Map<String, Object> expected, Object actual) {
        final var actualJSObject = doAssertIsObject(actual);
        assertEquals(expected.keySet().size(), actualJSObject.keySet().size());
        for (Entry<String, Object> expectedEntry : expected.entrySet()) {
            doAssertEquals(expectedEntry.getValue(), actualJSObject.getMember(expectedEntry.getKey()));
        }
    }

    private void doAssertEquals(TestPojo expected, Object actual) {
        final var actualJSObject = doAssertIsObject(actual);
        assertEquals(expected.booleanField, actualJSObject.getMember("booleanField"));
        assertEquals(expected.intField, actualJSObject.getMember("intField"));
        assertEquals(expected.longField, actualJSObject.getMember("longField"));
        assertEquals(expected.doubleField, actualJSObject.getMember("doubleField"));
        assertEquals(expected.booleanWrapperField, actualJSObject.getMember("booleanWrapperField"));
        assertEquals(expected.intWrapperField, actualJSObject.getMember("intWrapperField"));
        assertEquals(expected.longWrapperField, actualJSObject.getMember("longWrapperField"));
        assertEquals(expected.doubleWrapperField, actualJSObject.getMember("doubleWrapperField"));
        doAssertEquals(expected.booleanArrayField, actualJSObject.getMember("booleanArrayField"));
        doAssertEquals(expected.intArrayField, actualJSObject.getMember("intArrayField"));
        doAssertEquals(expected.longArrayField, actualJSObject.getMember("longArrayField"));
        doAssertEquals(expected.doubleArrayField, actualJSObject.getMember("doubleArrayField"));
        assertEquals(expected.stringField, actualJSObject.getMember("stringField"));
        doAssertEquals(expected.objectField, actualJSObject.getMember("objectField"));
        doAssertEquals(expected.arrayField, actualJSObject.getMember("arrayField"));
        doAssertEquals(expected.collectionField, actualJSObject.getMember("collectionField"));
        doAssertEquals(expected.mapField, actualJSObject.getMember("mapField"));
    }


    private JSObject doAssertIsArray(Object actual) {
        assertTrue(actual instanceof JSObject);
        final var actualJSObject = (JSObject) actual;
        assertTrue(actualJSObject.isArray());
        assertFalse(actualJSObject.isFunction());
        return actualJSObject;
    }

    private JSObject doAssertIsObject(Object actual) {
        assertTrue(actual instanceof JSObject);
        final var actualJSObject = (JSObject) actual;
        assertFalse(actualJSObject.isArray());
        assertFalse(actualJSObject.isFunction());
        return actualJSObject;
    }


    private TestPojo generateTestPojo(int depth) {
        var builder = TestPojo.builder();
        builder
            .booleanField(nextBoolean())
            .intField(nextInt())
            .longField(nextLong())
            .doubleField(nextDouble())
            .booleanWrapperField(nextBoolean())
            .intWrapperField(nextInt())
            .longWrapperField(nextLong())
            .doubleWrapperField(nextDouble())
            .booleanArrayField(nextBooleanArray(32))
            .intArrayField(nextIntArray(32))
            .longArrayField(nextLongArray(32))
            .doubleArrayField(nextDoubleArray(32))
            .stringField(nextHexString(32));
        if (depth > 0) {
            final int childDepth = depth - 1;
            builder
                .objectField(generateTestPojo(childDepth))
                .arrayField(generateTestArray(childDepth))
                .collectionField(generateTestCollection(childDepth))
                .mapField(generateTestMap(childDepth));
        }
        return builder.build();
    }

    private Object[] generateTestArray(int depth) {
        var array = new Object[]{
            nextBoolean(),
            nextInt(),
            nextLong(),
            nextDouble(),
            nextBooleanArray(32),
            nextIntArray(32),
            nextLongArray(32),
            nextDoubleArray(32),
            nextHexString(32)
        };
        if (depth > 0) {
            final int childDepth = depth - 1;
            array = Arrays.copyOf(array, array.length + 4);
            array[array.length - 4] = generateTestPojo(childDepth);
            array[array.length - 3] = generateTestArray(childDepth);
            array[array.length - 2] = generateTestCollection(childDepth);
            array[array.length - 1] = generateTestMap(childDepth);
        }
        return array;
    }

    private Collection<Object> generateTestCollection(int depth) {
        return ImmutableList.copyOf((generateTestArray(depth)));
    }

    private Map<String, Object> generateTestMap(int depth) {
        final var map = new HashMap<String, Object>();
        map.put("booleanKey", nextBoolean());
        map.put("intKey", nextInt());
        map.put("longKey", nextLong());
        map.put("doubleKey", nextDouble());
        map.put("booleanArrayKey", nextBooleanArray(32));
        map.put("intArrayKey", nextIntArray(32));
        map.put("longArrayKey", nextLongArray(32));
        map.put("doubleArrayKey", nextDoubleArray(32));
        map.put("stringKey", nextHexString(32));
        if (depth > 0) {
            final int childDepth = depth - 1;
            map.put("pojoKey", generateTestPojo(childDepth));
            map.put("arrayKey", generateTestArray(childDepth));
            map.put("collectionKey", generateTestCollection(childDepth));
            map.put("mapKey", generateTestMap(childDepth));
        }
        return Collections.unmodifiableMap(map);
    }


    @Getter
    @Setter
    @Builder
    private static class TestPojo {

        private boolean booleanField;
        private int intField;
        private long longField;
        private double doubleField;
        private Boolean booleanWrapperField;
        private Integer intWrapperField;
        private Long longWrapperField;
        private Double doubleWrapperField;
        private boolean[] booleanArrayField;
        private int[] intArrayField;
        private long[] longArrayField;
        private double[] doubleArrayField;
        private String stringField;
        private Object objectField;
        private Object[] arrayField;
        private Collection<Object> collectionField;
        private Map<String, Object> mapField;
    }
}
