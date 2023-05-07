package org.mocka.util;

import java.security.SecureRandom;
import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtils {

    private static final char[] HEX_CHARS = new char[]{
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final Random rnd = new SecureRandom();


    public static boolean nextBoolean() {
        return rnd.nextBoolean();
    }

    public static byte nextByte() {
        return (byte) rnd.nextInt();
    }

    public static short nextShort() {
        return (short) rnd.nextInt();
    }

    public static int nextInt() {
        return rnd.nextInt();
    }

    public static long nextLong() {
        return rnd.nextLong();
    }

    public static float nextFloat() {
        return rnd.nextFloat();
    }

    public static double nextDouble() {
        return rnd.nextDouble();
    }

    public static char nextChar() {
        return (char) rnd.nextInt();
    }

    public static boolean[] nextBooleanArray(int length) {
        final var booleans = new boolean[length];
        for (int i = 0; i < length; ) {
            booleans[i++] = rnd.nextBoolean();
        }
        return booleans;
    }

    public static byte[] nextByteArray(int length) {
        final var bytes = new byte[length];
        for (int i = 0; i < length; ) {
            for (int r = rnd.nextInt(), n = Math.min(length - i, 4); n-- > 0; r >>= 8) {
                bytes[i++] = (byte) r;
            }
        }
        return bytes;
    }

    public static short[] nextShortArray(int length) {
        final var shorts = new short[length];
        for (int i = 0; i < length; ) {
            for (int r = rnd.nextInt(), n = Math.min(length - i, 2); n-- > 0; r >>= 16) {
                shorts[i++] = (short) r;
            }
        }
        return shorts;
    }

    public static int[] nextIntArray(int length) {
        final var ints = new int[length];
        for (int i = 0; i < length; ) {
            ints[i++] = rnd.nextInt();
        }
        return ints;
    }

    public static long[] nextLongArray(int length) {
        final var longs = new long[length];
        for (int i = 0; i < length; ) {
            longs[i++] = rnd.nextLong();
        }
        return longs;
    }

    public static float[] nextFloatArray(int length) {
        final var floats = new float[length];
        for (int i = 0; i < length; ) {
            floats[i++] = rnd.nextFloat();
        }
        return floats;
    }

    public static double[] nextDoubleArray(int length) {
        final var doubles = new double[length];
        for (int i = 0; i < length; ) {
            doubles[i++] = rnd.nextFloat();
        }
        return doubles;
    }

    public static char[] nextCharArray(int length) {
        final var chars = new char[length];
        for (int i = 0; i < length; ) {
            for (int r = rnd.nextInt(), n = Math.min(length - i, 2); n-- > 0; r >>= 16) {
                chars[i++] = (char) r;
            }
        }
        return chars;
    }

    public static String nextString(int length) {
        return new String(nextCharArray(length));
    }

    public static String nextHexString(int length) {
        char[] chars = new char[length * 2];
        int i = 0;
        for (byte b : nextByteArray(length)) {
            chars[i++] = HEX_CHARS[b >> 4 & 0xF];
            chars[i++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }
}
