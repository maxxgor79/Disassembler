package ru.zxspectrum.disassembler.util;

import ru.zxspectrum.disassembler.lang.Type;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public final class TypeUtil {
    private TypeUtil() {

    }

    public static boolean isNumberPattenSymbol(int ch) {
        return ch == 'n';
    }

    public static boolean isAddressPatternSymbol(int ch) {
        return ch == 'a';
    }

    public static boolean isAddressOffsetPatternSymbol(int ch) {
        return ch == 'o';
    }

    public static boolean isOffsetPatternSymbol(int ch) {
        return ch == 'd';
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
        return getPatternSymbolCount(s, 'n');
    }

    public static int getAddressPatternSymbolCount(String s) {
        return getPatternSymbolCount(s, 'a');
    }

    public static int getOffsetPatternSymbolCount(String s) {
        return getPatternSymbolCount(s, 'd');
    }

    public static int getAddressOffsetPatternSymbolCount(String s) {
        return getPatternSymbolCount(s, 'o');
    }

    public static Type getPatternType(String s) {
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

}
