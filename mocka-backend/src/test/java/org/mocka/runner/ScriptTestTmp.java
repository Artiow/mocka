package org.mocka.runner;

import static org.mocka.util.Sneaks.sneakyRunnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Comparators;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.util.concurrent.MoreExecutors;
import com.sun.management.ThreadMXBean;
import io.minio.MinioClient;
import io.swagger.models.auth.In;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.SecureRandom;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mocka.MockaApplication;
import org.mocka.configuration.ScriptEngineConfiguration;
import org.mocka.configuration.SwaggerConfiguration;
import org.mocka.properties.ScriptEngineProperties;
import org.mocka.runner.marshaller.JSObjectFactory;
import org.mocka.runner.marshaller.JSObjectMarshaller;
import org.mocka.util.Formatter;
import org.mocka.util.RandomUtils;
import org.mocka.util.ResourceFileUtils;
import org.mocka.util.Sneaks;
import org.mocka.util.Stopwatches;
import org.mocka.util.ThreadMonitor;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Security;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import sun.misc.Unsafe;

@Slf4j
@SpringBootTest(classes = {
    ObjectMapper.class,
    JSObjectMarshaller.class,
    ScriptEngineConfiguration.class})
@EnableConfigurationProperties(value = {ScriptEngineProperties.class})
public class ScriptTestTmp {

    private static final Format FORMAT = new DecimalFormat("#0.000",
        DecimalFormatSymbols.getInstance(Locale.US));
    private static final double NANO_TO_MILLIS_FACTOR = 1.0d / 1000000.0d;

    @Autowired
    private JSObjectMarshaller marshaller;
    @Autowired
    private ScriptEngine scriptEngine;


    @RequiredArgsConstructor
    private static class FibonacciTack extends RecursiveTask<Integer> {

        private final int n;

        @Override
        protected Integer compute() {
            if (n <= 1) return n;
            final var f1 = new FibonacciTack(n - 1).fork();
            final var f2 = new FibonacciTack(n - 2);
            return f2.compute() + f1.join();
        }
    }

    @RequiredArgsConstructor(staticName = "handler")
    private static class Handler implements BiFunction<Object, Throwable, Void> {

        private final Consumer<Throwable> consumer;

        @Override
        public Void apply(Object unused, Throwable throwable) {
            if (throwable != null) {
                consumer.accept(throwable);
            }
            return null;
        }
    }


    private static class TestClassWithHolder {

        private static class Holder {

            private static final int zero = 0;
            private static final int undefined = 1 / 0;
        }


        public static int getHoldingZero() {
            return Holder.zero;
        }

        public static int getHoldingUndefined() {
            return Holder.undefined;
        }
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class TestSpliterator implements Spliterator<UUID> {

        private final UUID uuid = UUID.randomUUID();
        private int remain;

        @Override
        public boolean tryAdvance(Consumer<? super UUID> action) {
            if (remain-- > 0) {
                action.accept(uuid);
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<UUID> trySplit() {
            if (remain > 2) {
                final int newRemain = remain / 2;
                remain = remain - newRemain;
                return new TestSpliterator(newRemain);
            } else {
                return null;
            }
        }

        @Override
        public long estimateSize() { return remain; }

        @Override
        public int characteristics() { return NONNULL | SIZED | SUBSIZED | IMMUTABLE | CONCURRENT; }
    }


    interface A { }
    interface B { }
    interface C extends A, B { }


    @Test
    void testSpliterator() {
        final int s1 = StreamSupport
            .stream(new TestSpliterator(16), true)
            .peek(uuid -> log.info(uuid.toString()))
            .distinct()
            .collect(Collectors.toList())
            .size();
        final int s2 = StreamSupport
            .stream(new TestSpliterator(16), false)
            .peek(uuid -> log.info(uuid.toString()))
            .distinct()
            .collect(Collectors.toList())
            .size();

        log.info("parallel list size: {}", s1);
        log.info("non-parallel list size: {}", s2);
    }

    @Test
    void testArray() throws Exception {
        int[][][] arr = new int[6][5][4];
        println(String.valueOf(arr.length));
        println(String.valueOf(arr[0].length));
        println(String.valueOf(arr[0][0].length));
        arr = new int[1][][];
        println(String.valueOf(arr.length));
        println(Arrays.deepToString(arr));
        println(Arrays.deepToString(arr[0]));

        final var U = UnsafeUtils.getUnsafe();

        // NOTE: table for LITTLE_ENDIAN order
        final byte[] byteArray = {
            0x0f, 0x00, 0x00, 0x00, // 15
            0x09, 0x00, 0x00, 0x00, // 9
            0x0f, 0x0e, 0x00, 0x00, // 3599 (0x00000e0f)
            0x01, 0x00, 0x00, 0x00  // 1
        };

        final var INDEX_OFFSET = Unsafe.ARRAY_BYTE_INDEX_SCALE << 2;
        var offset = Unsafe.ARRAY_BYTE_BASE_OFFSET; // start
        Assertions.assertEquals(15, U.getInt(byteArray, offset));
        Assertions.assertEquals(0x0000000f, U.getInt(byteArray, offset));
        offset += INDEX_OFFSET;
        Assertions.assertEquals(9, U.getInt(byteArray, offset));
        Assertions.assertEquals(0x00000009, U.getInt(byteArray, offset));
        offset += INDEX_OFFSET;
        Assertions.assertEquals(3599, U.getInt(byteArray, offset));
        Assertions.assertEquals(0x00000e0f, U.getInt(byteArray, offset));
        offset += INDEX_OFFSET;
        Assertions.assertEquals(1, U.getInt(byteArray, offset));
        Assertions.assertEquals(0x00000001, U.getInt(byteArray, offset));

        final byte[] emptyByteArr = {0x7f};
        Assertions.assertEquals(127, U.getByte(emptyByteArr, Unsafe.ARRAY_BYTE_BASE_OFFSET));

        final var ORDER = ByteOrder.nativeOrder();
        println("\nbyte order: {}", ORDER);

        println(RandomUtils.toHexLine(getBytes(U, new byte[]{0x7f}, Unsafe.ARRAY_BYTE_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new byte[0], Unsafe.ARRAY_BYTE_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[0], Unsafe.ARRAY_INT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[1], Unsafe.ARRAY_INT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[2], Unsafe.ARRAY_INT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[15], Unsafe.ARRAY_INT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[16], Unsafe.ARRAY_INT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[255], Unsafe.ARRAY_INT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[256], Unsafe.ARRAY_INT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new int[255][255], Unsafe.ARRAY_OBJECT_BASE_OFFSET)));
        println("");
        println(RandomUtils.toHexLine(getBytes(U, new Object[0], Unsafe.ARRAY_OBJECT_BASE_OFFSET)));
        println(RandomUtils.toHexLine(getBytes(U, new Object[]{new Object()}, Unsafe.ARRAY_OBJECT_BASE_OFFSET + Unsafe.ARRAY_OBJECT_INDEX_SCALE)));
        println("");
        println(RandomUtils.toHexLine(getBytes(U, new Object[0], 16)));
        println(RandomUtils.toHexLine(getBytes(U, new Object[255], 16)));
        println(RandomUtils.toHexLine(getBytes(U, new Object(), 16)));
        println(RandomUtils.toHexLine(getBytes(U, new Object() { private final int value = 255; }, 16)));
        println(RandomUtils.toHexLine(getBytes(U, new Integer(1), 16)));
        println(RandomUtils.toHexLine(getBytes(U, new Integer(2), 16)));
        println("");

        final var obj = new Integer(3599); // 0x00000e0f
        println(RandomUtils.toHexLine(getBytes(U, obj, 16)));
        Assertions.assertEquals(0x00000e0f, U.getInt(obj, 12));
        println(RandomUtils.toHexLine(getBytes(U, obj, 12)));
        final var hash = System.identityHashCode(obj);
        println(RandomUtils.toHexLine(getBytes(U, obj, 12)));
        println("structure:");
        println(" mark: {}", RandomUtils.toHexLine(getBytes(U, obj, 8)));
        println(" hash:   {}", RandomUtils.toHexLine(hash, ORDER));
        println("klass:                 {}", RandomUtils.toHexLine(U.getInt(obj, 8), ORDER));
    }

    private byte[] getBytes(Unsafe u, Object obj, int length) {
        return getBytes(u, obj, length, 0);
    }

    private byte[] getBytes(Unsafe u, Object obj, int length, int offset) {
        final byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) { bytes[i] = u.getByte(obj, offset + i); }
        return bytes;
    }

    @Test
    void testLambda() throws Exception {
        final var pojo = new TestPojo();

        //final Runnable runnable = () -> { log.info(pojo.getUuid()); };
        final Runnable runnable = () -> log.info(pojo.getUuid());
        //final Runnable runnable = pojo::getUuid;
        //final Runnable runnable = TestClassWithHolder::getHoldingZero;
        //final Runnable runnable = new Runnable() { @Override public void run() { log.info(pojo.getUuid()); } };

        runnable.run();
        final var lambdaClass = runnable.getClass();
        println("lambda class: {}", lambdaClass.toString());
        println("fields:");
        for (Field field : lambdaClass.getDeclaredFields()) {
            println(field.toString());
        }
        println("constructors:");
        for (Constructor<?> constructor : lambdaClass.getDeclaredConstructors()) {
            println(constructor.toString());
        }
        println("methods:");
        for (Method method : lambdaClass.getDeclaredMethods()) {
            println(method.toString());
            if (method.getName().equals("get$Lambda")) {
                method.setAccessible(true);
                final var lambda1 = method.invoke(runnable, pojo);
                printRunnableLambdaInfo(lambda1, "\tlambda1 ");
                final var lambda2 = method.invoke(runnable, new TestPojo());
                printRunnableLambdaInfo(lambda2, "\tlambda2 ");
            }
        }
    }

    @SneakyThrows
    private static void printRunnableLambdaInfo(Object lambda, String printPrefix) {
        final var lambdaClass = lambda.getClass();
        println("{}class: {}", printPrefix, lambdaClass.toString());
        println("{}superclass: {}", printPrefix, lambdaClass.getSuperclass());
        println("{}interfaces: {}", printPrefix, Arrays.deepToString(lambdaClass.getInterfaces()));
        println("{}hash: {}", printPrefix, System.identityHashCode(lambda));
        println("{}run() invocation...", printPrefix); lambdaClass.getMethod("run").invoke(lambda);
    }

    private static void println(String format, Object... args) {
        System.out.println(Formatter.format(format, args));
    }

    @Test
    public void testInitialization() {
        log.info(TestClassWithHolder.class.getName());
        log.info(TestClassWithHolder.Holder.class.getName());

        log.info("zero: {}", TestClassWithHolder.getHoldingZero());
        try {
            log.info("undefined: {}", TestClassWithHolder.getHoldingUndefined());
        } catch (Throwable e) {
            log.info("undefined: threw {}", e.toString(), e);
        }

        log.info("zero: {}", TestClassWithHolder.getHoldingZero());
        try {
            log.info("undefined: {}", TestClassWithHolder.getHoldingUndefined());
        } catch (Throwable e) {
            log.info("undefined: threw {}", e.toString(), e);
        }

        log.info("zero: {}", TestClassWithHolder.getHoldingZero());
        try {
            log.info("undefined: {}", TestClassWithHolder.getHoldingUndefined());
        } catch (Throwable e) {
            log.info("undefined: threw {}", e.toString(), e);
        }

        log.info(TestClassWithHolder.class.getName());
        log.info(TestClassWithHolder.Holder.class.getName());
    }


    @Test
    void testPool() {
        final var dummy = new Object();
        final var hex = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };

//        final var map_declaredKeys = new WeakHashMap<String, Object>();
        final var map_constructedKeys_usePool = new WeakHashMap<String, Object>();
        final var map_constructedKeys_trueWeak = new WeakHashMap<String, Object>();
        final var map_intKeys_usePool = new WeakHashMap<Integer, Object>();
        final var map_intKeys_trueWeak = new WeakHashMap<Integer, Object>();

//        for (String declaredKey : getDeclaredKeys()) {
//            map_declaredKeys.put(declaredKey, dummy);
//        }
        declareKeys();
        for (int i = 0; i < 16; i++) {
            map_constructedKeys_usePool.put(("key_" + hex[i]).intern(), dummy);
        }
        for (int i = 0; i < 16; i++) {
            map_constructedKeys_trueWeak.put(("key_" + hex[i]), dummy);
        }
        for (int i = 0; i < 16; i++) {
            map_intKeys_usePool.put(i, dummy);
        }
        for (int i = 0; i < 16; i++) {
            map_intKeys_trueWeak.put(new Integer(i), dummy);
        }

        System.gc();
        do { } while (!map_constructedKeys_trueWeak.isEmpty() && !map_intKeys_trueWeak.isEmpty());

//        Assertions.assertEquals(map_declaredKeys.size(), 16);
        Assertions.assertEquals(map_constructedKeys_usePool.size(), 16);
        Assertions.assertTrue(map_constructedKeys_trueWeak.isEmpty());
        Assertions.assertEquals(map_intKeys_usePool.size(), 16);
        Assertions.assertTrue(map_intKeys_trueWeak.isEmpty());
    }

    String[] getGeneratedKeys(int n) {//, IntFunction<String> mapper) {
        final var keys = new ArrayList<String>(n);
        for (int i = 0; i < n; i++) { keys.add(String.valueOf(i)); }
        return keys.toArray(String[]::new);
        //IntStream
        // return IntStream.range(0, n).mapToObj(mapper).toArray(String[]::new);
    }

//    String[] getDeclaredKeys() {
    void declareKeys() {
        final String key_0 = "key_0";
        final String key_1 = "key_1";
        final String key_2 = "key_2";
        final String key_3 = "key_3";
        final String key_4 = "key_4";
        final String key_5 = "key_5";
        final String key_6 = "key_6";
        final String key_7 = "key_7";
        final String key_8 = "key_8";
        final String key_9 = "key_9";
        final String key_a = "key_a";
        final String key_b = "key_b";
        final String key_c = "key_c";
        final String key_d = "key_d";
        final String key_e = "key_e";
        final String key_f = "key_f";
//        return new String[]{
//            key_0, key_1, key_2, key_3, key_4, key_5, key_6, key_7,
//            key_8, key_9, key_a, key_b, key_c, key_d, key_e, key_f
//        };
    }

    @Test
    void testMemory4() throws Exception {
        final var RANDOM = new SecureRandom();

        final var cache = Class.forName("java.lang.Integer$IntegerCache");
        final var lowField = cache.getDeclaredField("low"); lowField.setAccessible(true);
        final var LOW = lowField.getInt(null);
        final var highField = cache.getDeclaredField("high"); highField.setAccessible(true);
        final var HIGH = highField.getInt(null);

        final var SIZE = HIGH - LOW + 1;
        Assertions.assertFalse(SIZE < 256, "Illegal int cache size: " + SIZE + " < 256");

        try {
            final var impossibleArray = new int[SIZE][0x007fffff];
            Assertions.fail();
        } catch (AssertionError assertionError) {
            throw assertionError;
        } catch (Error err) {
            Assertions.assertEquals(OutOfMemoryError.class, err.getClass());
            Assertions.assertEquals("Java heap space", err.getMessage());
        } catch (Throwable thr) {
            Assertions.fail(thr);
        }

        try {
            final var map = new HashMap<Integer, int[]>();
            for (int i = 0; i < SIZE; i++) { map.put(RANDOM.nextInt(), new int[0x007fffff]); }
            Assertions.fail(String.valueOf(map.size()));
        } catch (AssertionError assertionError) {
            throw assertionError;
        } catch (Error err) {
            Assertions.assertEquals(OutOfMemoryError.class, err.getClass());
            Assertions.assertEquals("Java heap space", err.getMessage());
        } catch (Throwable thr) {
            Assertions.fail(thr);
        }

        {
            final var map = new WeakHashMap<Integer, int[]>();
            for (int i = 0; i < SIZE; i++) { map.put(RANDOM.nextInt(), new int[0x007fffff]); }
            Assertions.assertTrue(map.size() < SIZE);
        }

        try {
            final var map = new WeakHashMap<Integer, int[]>();
            for (int i = LOW; i < HIGH; i++) { map.put(Integer.valueOf(i), new int[0x007fffff]); }
            Assertions.fail(String.valueOf(map.size()));
        } catch (AssertionError assertionError) {
            throw assertionError;
        } catch (Error err) {
            Assertions.assertEquals(OutOfMemoryError.class, err.getClass());
            Assertions.assertEquals("Java heap space", err.getMessage());
        } catch (Throwable thr) {
            Assertions.fail(thr);
        }

        {
            final var map = new WeakHashMap<Integer, int[]>();
            for (int i = LOW; i < HIGH; i++) { map.put(new Integer(i), new int[0x007fffff]); }
            Assertions.assertTrue(map.size() < SIZE);
        }

        try {
            final var map = new WeakHashMap<String, int[]>();
            for (int i = LOW; i < HIGH; i++) { map.put(String.valueOf(i).intern(), new int[0x007fffff]); }
            Assertions.fail(String.valueOf(map.size()));
        } catch (AssertionError assertionError) {
            throw assertionError;
        } catch (Error err) {
            Assertions.assertEquals(OutOfMemoryError.class, err.getClass());
            Assertions.assertEquals("Java heap space", err.getMessage());
        } catch (Throwable thr) {
            Assertions.fail(thr);
        }

        {
            final var map = new WeakHashMap<String, int[]>();
            for (int i = LOW; i < HIGH; i++) { map.put(String.valueOf(i), new int[0x007fffff]); }
            Assertions.assertTrue(map.size() < SIZE);
        }
    }


    @Test
    void testMemory3() {
        final var intString = "110";
        final var intStringFromPool = Integer.valueOf(110).toString().intern();
        final var intStringStandalone = Integer.valueOf(110).toString();

        Assertions.assertSame(intString, intStringFromPool);
        Assertions.assertNotSame(intString, intStringStandalone);
    }

    @Test
    void testClassLoader() throws Exception {
        final var thisClassLoader = this.getClass().getClassLoader();
        log.info("{}", thisClassLoader.getClass());
        log.info("{}", Optional.of(thisClassLoader).map(ClassLoader::getParent).map(Object::getClass).orElse(null));
        log.info("{}", Optional.of(thisClassLoader).map(ClassLoader::getParent).map(ClassLoader::getParent).map(Object::getClass).orElse(null));




        logClassInfo(ClassLoader.class);
        logClassInfo(List.class);
        logClassInfo(ArrayList.class);
        logClassInfo(BigInteger.class);
        logClassInfo(LocalDateTime.class);
        logClassInfo(DriverManager.class);
        logClassInfo(ScriptEngine.class);
        logClassInfo(MinioClient.class);
        logClassInfo(MockaApplication.class);

        logClassInfo(Thread.class);
        logClassInfo(Thread.currentThread().getContextClassLoader().getClass());

        try {
            final var thisClass = this.getClass();
            logClassInfo(thisClass);
            final var loadedClass = thisClass.getClassLoader().loadClass(thisClass.getName());
            logClassInfo(loadedClass);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        try {
            logClassInfo(Class.forName("java.NonExistingClass"));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        final var appClass = MockaApplication.class;
        log.info("        appClass: {}@{}", appClass.getName(), System.identityHashCode(appClass));
        logClassInfo(appClass);
        final var appClassUrl = appClass.getProtectionDomain().getCodeSource().getLocation();
        final var urlClassLoader = new URLClassLoader(new URL[]{appClassUrl}, this.getClass().getClassLoader().getParent());
        final var reloadedAppClass = urlClassLoader.loadClass(appClass.getName());
        log.info("reloadedAppClass: {}@{}", reloadedAppClass.getName(), System.identityHashCode(reloadedAppClass));
        logClassInfo(reloadedAppClass);
        final var appHeir = new MockaApplication() { };
        final var appHeirClass = appHeir.getClass();
        log.info("    appHeirClass: {}@{}", appHeirClass.getName(), System.identityHashCode(appHeirClass));
        logClassInfo(reloadedAppClass);
        log.info("        appClass.isInstance(new MockaApplication() { }) : {}", appClass.isInstance(appHeir));
        log.info("reloadedAppClass.isInstance(new MockaApplication() { }) : {}", reloadedAppClass.isInstance(appHeir));

        final var extUrl = new URL("file:/home/artiow/IdeaProjects/simple-messenger/eureka/target/classes/");
        final var extUrlClassLoader = new URLClassLoader(new URL[]{extUrl}, this.getClass().getClassLoader());
        final var extClassName = "com.artiow.examples.messenger.eureka.EurekaApplication";
        final var extClass = extUrlClassLoader.loadClass(extClassName);
        logClassInfo(extClass);

        final var extResource = IOUtils.toString(extUrlClassLoader.getResourceAsStream("application.yml"), StandardCharsets.UTF_8);
        System.out.println(extResource);

        final var newExtUrlClassLoader = new URLClassLoader(new URL[]{extUrl}, this.getClass().getClassLoader().getParent());
        final var newExtResource = IOUtils.toString(newExtUrlClassLoader.getResourceAsStream("application.yml"), StandardCharsets.UTF_8);
        System.out.println(newExtResource);
    }


    private <E> void logClassInfo(Class<E> testClass) {
        final var classInfo = classInfo(testClass);
        log.info("{}: {}", classInfo.getKey(), classInfo.getValue());
        log.info("source: {}", Optional.of(classInfo.getKey()).map(Class::getProtectionDomain).map(ProtectionDomain::getCodeSource).orElse(null));
    }

    private <E> Map.Entry<Class<E>, Class<? extends ClassLoader>> classInfo(Class<E> testClass) {
        return new SimpleEntry<>(
            testClass,
            Optional.of(testClass).map(Class::getClassLoader).map(ClassLoader::getClass).orElse(null));
    }


    private static class InfiniteLoopThread extends Thread {

        @Override public void run() { this.run(); }
    }

    @Test
    public void testMemory2() throws Exception {
        RandomUtils.nextInt();
        final var childThread = new Thread(() -> {
           while (true) {
               // allocate 1Mb
               RandomUtils.nextByteArray(1024 * 1024);
               Thread.yield();
           }
        });

        final var monitor = new ThreadMonitor(childThread);
        childThread.start();

        while (childThread.isAlive()) {
            log.info("bytes: {} byte", monitor.getThreadAllocatedBytes());
            log.info("  cpu: {} ms", Duration.ofNanos(monitor.getThreadCpuTime()).toMillis());
            System.out.println('\n');
            Thread.sleep(1000);
        }
    }

    @Test
    public void testMemory() throws Exception {
        RandomUtils.nextInt();
        final Set<Object> set = new HashSet<>(1);

        final var childThread = new Thread(() -> {
            // do nothing
            LockSupport.park();
            // do nothing once more
            LockSupport.park();
            // allocate 1Mb
            RandomUtils.nextByteArray(1024 * 1024);
            LockSupport.park();
            // allocate another 1Mb
            set.add(RandomUtils.nextByteArray(1024 * 1024));
            LockSupport.park();
            // clear set
            set.clear();
            LockSupport.park();
        });

        final var monitor = new ThreadMonitor(childThread);
        childThread.start();
        int counter = 4;
        while (childThread.isAlive()) {
            log.info("bytes: {} byte", monitor.getThreadAllocatedBytes());
            log.info("  cpu: {} ms", Duration.ofNanos(monitor.getThreadCpuTime()).toMillis());
            if (set.isEmpty()) { log.info("set is empty"); }
            System.out.println('\n');

            if (counter-- > 0) { LockSupport.unpark(childThread); }
            Thread.sleep(2000);
        }
    }

    @Test
    void testConcurrency() throws Exception {
        var fibonacci = new FibonacciTack(32).fork();

        CompletableFuture.supplyAsync(() -> new long[Integer.MAX_VALUE])
            .handleAsync(Handler.handler((e) -> log.warn(e.toString())));
        CompletableFuture.supplyAsync(() -> new long[Integer.MAX_VALUE])
            .handleAsync(Handler.handler((e) -> log.warn(e.toString())));
        CompletableFuture.supplyAsync(() -> new long[Integer.MAX_VALUE])
            .handleAsync(Handler.handler((e) -> log.warn(e.toString()))).join();

        final var loopThread = new InfiniteLoopThread();
        loopThread.setUncaughtExceptionHandler((t, e) -> log.warn("trace length: {}", e.getStackTrace().length));
        loopThread.start();

        loopThread.join();

        log.info("fibonacci: {}", fibonacci.join());
    }

    @Test
    public void testConsole() throws Exception {

        final var emptyFunction = (JSObject) scriptEngine.eval("(arg) => { }", new SimpleScriptContext());
        final var logFunction = new Intruder(emptyFunction) {
            @Override
            public Object call(Object thiz, Object... args) {
                log.info("hooray! {}", args[0].toString());
                return super.call(thiz, args);
            }
        };

        final var consoleObject = JSObjectFactory.evaluated(scriptEngine).newObject();
        consoleObject.setMember("log", logFunction);

        final var scriptContext = new SimpleScriptContext();
        scriptContext.setAttribute("console", consoleObject, ScriptContext.ENGINE_SCOPE);

        final var t = new Thread(Sneaks.sneakyRunnable(() -> scriptEngine.eval(ResourceFileUtils.read("classpath:testConsole.js"), scriptContext)));
        t.start();

        Thread.sleep(1000);
        log.info("killing thread {}...", t.getName());
        t.stop();
        log.info("awaiting thread {} death...", t.getName());
        while (t.isAlive());
        log.info("thread {} has been killed. state: {}", t.getName(), t.getState());
    }

    @Test
    public void testThreadPool() throws InterruptedException {
        final var parallelism = Runtime.getRuntime().availableProcessors();
        final var factory = new ForkJoinWorkerThreadFactory() {
            @Override
            public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                var newThread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                log.info(
                    "Thread {} ({}) has been created",
                    newThread.getName(),
                    Integer.toHexString(newThread.hashCode()));
                return newThread;
            }
        };

        final var pool = new ForkJoinPool(parallelism, factory, null, false);

        Thread.sleep(5000);
        pool.execute(() -> {
            log.info(
                "First execution in {} ({})",
                Thread.currentThread().getName(),
                Integer.toHexString(Thread.currentThread().hashCode()));
        });

        Thread.sleep(5000);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                log.info(
                    "Execution in {} ({})",
                    Thread.currentThread().getName(),
                    Integer.toHexString(Thread.currentThread().hashCode()));
            });
        }

        Thread.sleep(5000);
        pool.execute(() -> {
            throw new RuntimeException(Formatter.format(
                "{} ({})",
                Thread.currentThread().getName(),
                Integer.toHexString(Thread.currentThread().hashCode())));
        });

        Thread.sleep(5000);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                log.info(
                    "Execution in {} ({})",
                    Thread.currentThread().getName(),
                    Integer.toHexString(Thread.currentThread().hashCode()));
            });
        }

        Thread.sleep(5000);
        log.info("exit");
    }

    @Test
    public void testByte() {
        log.info("{}", (byte) ((0x01 << 1) + 1));
        log.info("{}", RandomUtils.nextHexString(8));
    }

    @Test
    public void testAbuse() throws Exception {
        var interceptor = new Interceptor(
            (JSObject) scriptEngine.eval("function interceptor() {\n\n}\ninterceptor"));

        // CompletableFuture.supplyAsync()

        final ThreadMXBean threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        final var scriptReader = ResourceFileUtils.read("classpath:testAbuse_empty.js");

        log.info("threadMXBean info:\n total bytes: {}",
            LongStream.of(threadMXBean.getThreadAllocatedBytes(threadMXBean.getAllThreadIds()))
                .sum());

        final var context = new SimpleScriptContext();
        context.setAttribute("interceptor", interceptor, ScriptContext.ENGINE_SCOPE);
        final var evilThread = new Thread(sneakyRunnable(() -> {
            try {
                scriptEngine.eval(scriptReader, context);
            } catch (ThreadDeath threadDeath) {
                log.info("Death detected: {}", threadDeath.getMessage(), threadDeath);
                throw threadDeath; // important to rethrow ThreadDeath
            }
        }));

        final double msFactor = 1 / 1000000.0d;
        final var formatter = new DecimalFormat("#0.000",
            DecimalFormatSymbols.getInstance(Locale.US));

        final var monitor = new ThreadMonitor(evilThread);
        evilThread.start();

        final var startTime = System.nanoTime();
        long totalAllocatedBytes = LongStream.of(
            threadMXBean.getThreadAllocatedBytes(threadMXBean.getAllThreadIds())).sum();
        long threadAllocatedBytes = monitor.getThreadAllocatedBytes();
        long threadCpuTime = monitor.getThreadCpuTime();
        long wallTime = System.nanoTime() - startTime;
        log.info(
            "thread monitor info:\n total bytes: {}\n thread bytes: {}\n cpu time: {}ms\n wall time: {}ms",
            totalAllocatedBytes,
            threadAllocatedBytes,
            formatter.format(msFactor * threadCpuTime),
            formatter.format(msFactor * wallTime));

        Thread.sleep(2500);
        evilThread.stop();

        while (evilThread.isAlive()) {
            try {
                totalAllocatedBytes = LongStream.of(
                    threadMXBean.getThreadAllocatedBytes(threadMXBean.getAllThreadIds())).sum();
                threadAllocatedBytes = monitor.getThreadAllocatedBytes();
                threadCpuTime = monitor.getThreadCpuTime();
                wallTime = System.nanoTime() - startTime;
            } catch (IllegalStateException ignored) {

            }
        }
        log.info(
            "thread monitor info:\n total bytes: {}\n thread bytes: {}\n cpu time: {}ms\n wall time: {}ms",
            totalAllocatedBytes,
            threadAllocatedBytes,
            formatter.format(msFactor * threadCpuTime),
            formatter.format(msFactor * wallTime));

        //final var evilArray = (JSObject) scriptEngine.eval("evilArray", context);
        //log.info("\n evilArray size: {}", evilArray.keySet().size());
    }

    @Test
    public void test() throws Exception {
        var testFunctionFile = ResourceFileUtils.open("classpath:testFunction.js");
        var testFile = ResourceFileUtils.open("classpath:test.js");
        var context1 = new SimpleScriptContext();
        var context2 = new SimpleScriptContext();

        var testFunction = scriptEngine.eval(new InputStreamReader(testFunctionFile),
            new SimpleScriptContext());
        testFunction = new Intruder((JSObject) testFunction);

        context1.setAttribute("a", "myValue", ScriptContext.ENGINE_SCOPE);
        context1.setAttribute("testFunction", new TestFunction(), ScriptContext.ENGINE_SCOPE);

        var result = scriptEngine.eval(new InputStreamReader(testFile), context1);
        log.info("result: {}", result);
        log.info("result uuid: {}", ((TestPojo) result).getUuid());
        log.info("result id: {}", System.identityHashCode(result));
    }

    @Test
    public void testPermissions() throws Exception {
//        final var threadId = Thread.currentThread().getId();
//        final var threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
//        log.info("bytes: {}", threadMXBean.getThreadAllocatedBytes(threadId));

        var scriptFile = ResourceFileUtils.open("classpath:testPermissions.js");
        log.info("{}", scriptEngine.eval(new InputStreamReader(scriptFile)));
    }

    @Test
    public void testMeasure() throws Exception {
        try (var reader = ResourceFileUtils.read("classpath:testAbuse_8mb.js")) {
            final var flag = new Object() {
                private boolean flag = false;

                public void set(boolean value) {
                    flag = value;
                }

                public boolean get() {
                    return flag;
                }
            };

            final var thread = new Thread(Sneaks.sneakyRunnable(() -> {
                scriptEngine.eval(reader);
                Thread.sleep(10000);
                flag.set(true);
            }));

            final var monitor = new ThreadMonitor(thread);

            thread.start();
            while (!flag.get()) {
                final var bytes = monitor.getThreadAllocatedBytes();
                final var time = monitor.getThreadCpuTime();
                log.info(
                    "Info:\n allocated: {} {}\n time: {} ms",
                    bytes > 1024 ? DataSize.ofMegabytes(bytes).toKilobytes() : bytes,
                    bytes > 1024 ? "Kb" : "b",
                    FORMAT.format(NANO_TO_MILLIS_FACTOR * time));

                Thread.sleep(100);
            }

            log.info("exit");
        }
    }


    private static class Interceptor extends Intruder {

        public Interceptor(JSObject realObject) {
            super(realObject);
        }

        @Override
        public Object call(Object thiz, Object... args) {
            return true;
        }
    }

    private static class TestPojo {

        @Getter
        private final String uuid;

        public TestPojo() {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    private static class TestFunction implements JSObject {

        @Override
        public Object call(Object thiz, Object... args) {
            log.info("TestFunction call: {}", args[0]);
            var pojo = new TestPojo();
            log.info("pojo: {}", pojo);
            log.info("pojo uuid: {}", pojo.getUuid());
            log.info("pojo id: {}", System.identityHashCode(pojo));
            return pojo;
        }

        @Override
        public Object newObject(Object... args) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object eval(String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getMember(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getSlot(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasMember(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasSlot(int slot) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeMember(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMember(String name, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSlot(int index, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Object> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInstance(Object instance) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInstanceOf(Object clazz) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getClassName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isFunction() {
            return true;
        }

        @Override
        public boolean isStrictFunction() {
            return false;
        }

        @Override
        public boolean isArray() {
            throw new UnsupportedOperationException();
        }
    }


    @RequiredArgsConstructor
    private static class Intruder implements JSObject {

        private final JSObject realObject;

        @Override
        public Object call(Object thiz, Object... args) {
            return realObject.call(thiz, args);
        }

        @Override
        public Object newObject(Object... args) {
            return realObject.newObject(args);
        }

        @Override
        public Object eval(String s) {
            return realObject.eval(s);
        }

        @Override
        public Object getMember(String name) {
            return realObject.getMember(name);
        }

        @Override
        public Object getSlot(int index) {
            return realObject.getSlot(index);
        }

        @Override
        public boolean hasMember(String name) {
            return realObject.hasMember(name);
        }

        @Override
        public boolean hasSlot(int slot) {
            return realObject.hasSlot(slot);
        }

        @Override
        public void removeMember(String name) {
            realObject.removeMember(name);
        }

        @Override
        public void setMember(String name, Object value) {
            realObject.setMember(name, value);
        }

        @Override
        public void setSlot(int index, Object value) {
            realObject.setSlot(index, value);
        }

        @Override
        public Set<String> keySet() {
            return realObject.keySet();
        }

        @Override
        public Collection<Object> values() {
            return realObject.values();
        }

        @Override
        public boolean isInstance(Object instance) {
            return realObject.isInstance(instance);
        }

        @Override
        public boolean isInstanceOf(Object clazz) {
            return realObject.isInstanceOf(clazz);
        }

        @Override
        public String getClassName() {
            return realObject.getClassName();
        }

        @Override
        public boolean isFunction() {
            return realObject.isFunction();
        }

        @Override
        public boolean isStrictFunction() {
            return realObject.isStrictFunction();
        }

        @Override
        public boolean isArray() {
            return realObject.isArray();
        }

        @Override
        @Deprecated
        public double toNumber() {
            return realObject.toNumber();
        }

        @Override
        public Object getDefaultValue(Class<?> hint) throws UnsupportedOperationException {
            return realObject.getDefaultValue(hint);
        }
    }
}
