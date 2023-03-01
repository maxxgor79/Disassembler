package ru.zxspectrum.disassembler.io;

import ru.zxspectrum.disassembler.i18n.Messages;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class Output {
    private static final String FORMAT1 = "%s %s: [%d] %s";

    private static final String FORMAT2 = "%s : [%d] %s";

    public static String errorFormat(String sourceName, int line, String message, Object ... args) {
        String error = Messages.getMessage(Messages.ERROR);
        return String.format(FORMAT1, error, sourceName, line, String.format(message, args));
    }

    public static String errorFormat(int line, String message, Object ... args) {
        String error = Messages.getMessage(Messages.ERROR);
        return String.format(FORMAT2, error, line, String.format(message, args));
    }

    public static String warningFormat(String sourceName, int line, String message, Object ... args) {
        String error = Messages.getMessage(Messages.WARNING);
        return String.format(FORMAT1, error, sourceName, line, String.format(message, args));
    }

    public static String warningFormat(int line, String message, Object ... args) {
        String error = Messages.getMessage(Messages.WARNING);
        return String.format(FORMAT2, error, line, String.format(message, args));
    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static void println(String format, String ... args) {
        System.out.println(String.format(format, args));
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static void print(String format, String ... args) {
        System.out.print(String.format(format, args));
    }

}
