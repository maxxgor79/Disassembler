package ru.zxspectrum.disassembler.lang;

/**
 * @Author Maxim Gorin
 */
public enum Type {
    Int8, UInt8, Int16, UInt16, Int32, UInt32, Int64, UInt64, Unknown;

    public static int sizeOf(Type type) {
        return switch (type) {
            case Int8, UInt8 -> 1;
            case Int16, UInt16 -> 2;
            case Int32, UInt32 -> 4;
            case Int64,UInt64 -> 8;
            case Unknown -> 0;
        };
    }
}
