package org.mocka.util;

import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtils {

    private static final char[] HEX_CHARS = new char[]{
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final Random RANDOM = new SecureRandom();


    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }

    public static byte nextByte() {
        return (byte) RANDOM.nextInt();
    }

    public static short nextShort() {
        return (short) RANDOM.nextInt();
    }

    public static int nextInt() {
        return RANDOM.nextInt();
    }

    public static long nextLong() {
        return RANDOM.nextLong();
    }

    public static float nextFloat() {
        return RANDOM.nextFloat();
    }

    public static double nextDouble() {
        return RANDOM.nextDouble();
    }

    public static char nextChar() {
        return (char) RANDOM.nextInt();
    }

    public static boolean[] nextBooleanArray(int length) {
        final var booleans = new boolean[length];
        for (int i = 0; i < length; ) {
            booleans[i++] = RANDOM.nextBoolean();
        }
        return booleans;
    }

    public static byte[] nextByteArray(int length) {
        final var bytes = new byte[length];
        for (int i = 0; i < length; ) {
            for (int r = RANDOM.nextInt(), n = Math.min(length - i, 4); n-- > 0; r >>= 8) {
                bytes[i++] = (byte) r;
            }
        }
        return bytes;
    }

    public static short[] nextShortArray(int length) {
        final var shorts = new short[length];
        for (int i = 0; i < length; ) {
            for (int r = RANDOM.nextInt(), n = Math.min(length - i, 2); n-- > 0; r >>= 16) {
                shorts[i++] = (short) r;
            }
        }
        return shorts;
    }

    public static int[] nextIntArray(int length) {
        final var ints = new int[length];
        for (int i = 0; i < length; ) {
            ints[i++] = RANDOM.nextInt();
        }
        return ints;
    }

    public static long[] nextLongArray(int length) {
        final var longs = new long[length];
        for (int i = 0; i < length; ) {
            longs[i++] = RANDOM.nextLong();
        }
        return longs;
    }

    public static float[] nextFloatArray(int length) {
        final var floats = new float[length];
        for (int i = 0; i < length; ) {
            floats[i++] = RANDOM.nextFloat();
        }
        return floats;
    }

    public static double[] nextDoubleArray(int length) {
        final var doubles = new double[length];
        for (int i = 0; i < length; ) {
            doubles[i++] = RANDOM.nextFloat();
        }
        return doubles;
    }

    public static char[] nextCharArray(int length) {
        final var chars = new char[length];
        for (int i = 0; i < length; ) {
            for (int r = RANDOM.nextInt(), n = Math.min(length - i, 2); n-- > 0; r >>= 16) {
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
            chars[i++] = HEX_CHARS[b >> 4 & 0x0f];
            chars[i++] = HEX_CHARS[b & 0x0f];
        }
        return new String(chars);
    }

    public static String toHexLine(int value, ByteOrder order) {
        if (order.equals(ByteOrder.BIG_ENDIAN)) {
            return toHexLine(new byte[]{
                (byte) ((value >> 24) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) (value & 0xff)
            });
        } else {
            return toHexLine(new byte[]{
                (byte) (value & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 24 & 0xff))
            });
        }
    }

    public static String toHexLine(byte[] byteArray) {
        final char[] chars = new char[byteArray.length * 2];
        int i = 0;
        for (byte b : byteArray) {
            chars[i++] = HEX_CHARS[b >> 4 & 0x0f];
            chars[i++] = HEX_CHARS[b & 0x0f];
        }
        return new String(chars);
    }
}
