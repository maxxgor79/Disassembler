package ru.zxspectrum.disassembler.lang;

import lombok.Getter;
import lombok.NonNull;

import java.math.BigInteger;

/**
 * @Author Maxim Gorin
 */
public enum Type {
    Int8(1, -128, 127, new String[]{"d", "e"}), UInt8(1, 0, 255, new String[]{"n"})
    , Int16(2, -32768, 32767, new String[]{"dd", "ee"})
    , UInt16(2, 0, 65535, new String[]{"nn"})
    , Int32(4, Integer.MIN_VALUE, Integer.MAX_VALUE, new String[]{"dddd", "eeee"})
    , UInt32(4, 0, 0xFFFFFFFF, new String[]{"nnnn"}), Unknown(0, 0, 0, null);

    Type(int size, long min, long max, String[] masks) {
        this.size = size;
        this.min = min;
        this.max = max;
        this.masks = masks;
    }

    public static Type getUnsignedBySize(int size) {
        for (Type t : values()) {
            if ((t.size == size) && (t.getMin() >= 0)) {
                return t;
            }
        }
        return Unknown;
    }

    public static Type getSignedBySize(int size) {
        for (Type t : values()) {
            if ((t.size == size) && (t.getMin() < 0)) {
                return t;
            }
        }
        return Unknown;
    }

    private byte[] getByte(byte val) {
        byte[] a = new byte[1];
        a[0] = val;
        return a;
    }

    private byte[] getLEShort(short val) {
        byte[] a = new byte[2];
        a[0] = (byte) val;
        a[1] = (byte) (val >>> 8);
        return a;
    }

    private byte[] getLEInt(int val) {
        byte[] a = new byte[4];
        a[0] = (byte) val;
        a[1] = (byte) (val >>> 8);
        a[2] = (byte) (val >>> 16);
        a[3] = (byte) (val >>> 24);
        return a;
    }

    private byte[] getBEShort(short val) {
        byte[] a = new byte[2];
        a[1] = (byte) val;
        a[0] = (byte) (val >>> 8);
        return a;
    }

    private byte[] getBEInt(int val) {
        byte[] a = new byte[4];
        a[3] = (byte) val;
        a[2] = (byte) (val >>> 8);
        a[1] = (byte) (val >>> 16);
        a[0] = (byte) (val >>> 24);
        return a;
    }

    public byte[] getBytes(@NonNull BigInteger value, @NonNull ByteOrder order) {
        int result;
        if (order == ByteOrder.LittleEndian) {
            switch (this) {
                case Int8, UInt8 -> {
                    return getByte(value.byteValue());
                }
                case Int16, UInt16 -> {
                    return getLEShort(value.shortValue());
                }
                case Int32, UInt32 -> {
                    return getLEInt(value.shortValue());
                }
            }
        } else {
            switch (this) {
                case Int8, UInt8 -> {
                    return getByte(value.byteValue());
                }
                case Int16, UInt16 -> {
                    return getBEShort(value.shortValue());
                }

                case Int32, UInt32 -> {
                    return getBEInt(value.intValue());
                }
            }
        }
        throw new NumberFormatException();
    }

    public Type getByMask(@NonNull String mask) {
        if (!mask.trim().isEmpty()) {
            for (Type t : values()) {
                if (t.masks != null) {
                    for (String m : t.masks) {
                        if (mask.equals(m)) {
                            return t;
                        }
                    }
                }
            }
        }
        return Unknown;
    }

    @Getter
    private int size;

    @Getter
    private long min;

    @Getter
    private long max;

    @Getter
    private String[] masks;
}
