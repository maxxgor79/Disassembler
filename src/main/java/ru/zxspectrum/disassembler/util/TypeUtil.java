package ru.zxspectrum.disassembler.util;

import ru.zxspectrum.disassembler.lang.Type;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public final class TypeUtil {
    private TypeUtil() {

    }

    public static boolean isAddressPattern(String pattern) {
        return isPattern(pattern, getAddressPatternSymbol());
    }

    private static char getAddressPatternSymbol() {
        return 'a';
    }

    public static boolean isAddressOffsetPattern(String pattern) {
        return isPattern(pattern, getAddressOffsetPatternSymbol());
    }

    private static char getAddressOffsetPatternSymbol() {
        return 'o';
    }

    public static boolean isNumberPattern(String pattern) {
        return isPattern(pattern, getNumberPatternSymbol());
    }

    private static char getNumberPatternSymbol() {
        return 'n';
    }

    public static boolean isOffsetPattern(String pattern) {
        return isPattern(pattern, getOffsetPatternSymbol());
    }

    private static char getOffsetPatternSymbol() {
        return 'd';
    }

    public static boolean isNumberPattenSymbol(int ch) {
        return ch == getNumberPatternSymbol();
    }

    public static boolean isAddressPatternSymbol(int ch) {
        return ch == getAddressPatternSymbol();
    }

    public static boolean isAddressOffsetPatternSymbol(int ch) {
        return ch == getAddressOffsetPatternSymbol();
    }

    public static boolean isOffsetPatternSymbol(int ch) {
        return ch == getOffsetPatternSymbol();
    }

    private static int getPatternSymbolCount(String s, int ch) {
        int count = 0;
        for (int c : s.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }

    public static int getNumberPatternSymbolCount(String s) {
        return getPatternSymbolCount(s, getNumberPatternSymbol());
    }

    public static int getAddressPatternSymbolCount(String s) {
        return getPatternSymbolCount(s, getAddressPatternSymbol());
    }

    public static int getOffsetPatternSymbolCount(String s) {
        return getPatternSymbolCount(s, getOffsetPatternSymbol());
    }

    public static int getAddressOffsetPatternSymbolCount(String s) {
        return getPatternSymbolCount(s, getAddressOffsetPatternSymbol());
    }

    public static Type getParamPatternType(String s) {
        if (s == null || s.isEmpty()) {
            return Type.Unknown;
        }
        int size;
        boolean signed;
        int ch = s.charAt(0);
        if (isAddressPatternSymbol(ch)) {
            size = getAddressPatternSymbolCount(s);
            signed = false;
        }  else {
            if (isNumberPattenSymbol(ch)) {
                size = getNumberPatternSymbolCount(s);
                signed = false;
            } else {
                if (isAddressOffsetPatternSymbol(ch)) {
                    size = getAddressOffsetPatternSymbolCount(s);
                    signed = true;
                } else {
                    if (isOffsetPatternSymbol(ch)) {
                        size = getOffsetPatternSymbolCount(s);
                        signed = true;
                    } else {
                        return Type.Unknown;
                    }
                }
            }
        }
        switch(size) {
            case 1: return signed ? Type.Int8 : Type.UInt8;
            case 2: return signed ? Type.Int16 : Type.UInt16;
            case 4: return signed ? Type.Int32 : Type.UInt32;
            case 8: return signed ? Type.Int64 : Type.UInt64;
        }
        return Type.Unknown;
    }

    private static boolean isPattern(String pattern, char patternLetter) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }
        String name = pattern.toLowerCase();
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) != patternLetter) {
                return false;
            }
        }
        return true;
    }
}
