package ru.zxspectrum.disassembler.util;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public final class SymbolUtil {
    private SymbolUtil() {

    }

    public static boolean isHexadecimalDigit(int ch) {
        return "abcdefABCDEF0123456789".indexOf(ch) >= 0;
    }

    public static boolean isDollar(int ch) {
        return ch == '$';
    }

    public static String fillChar(char c, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count is negative");
        }
        StringBuilder sb = new StringBuilder();
        for (char i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String replace(String s1, int index, String s2) {
        if (s1 == null) {
            throw new NullPointerException("s1");
        }
        if (index < 0) {
            throw new IllegalArgumentException("index is negative");
        }
        if (s2 == null) {
            throw new NullPointerException("s2");
        }
        StringBuilder sb = new StringBuilder(s1);
        for (int i = 0; i < s2.length(); i++) {
            sb.setCharAt(i + index, s2.charAt(i));
        }
        return sb.toString();
    }

}
