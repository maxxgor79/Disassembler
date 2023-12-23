package ru.zxspectrum.disassembler.util;

import lombok.NonNull;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 * Date: 28.02.2023
 */
public final class ConverterUtil {
    private ConverterUtil() {

    }

    public static String toTextHexadecimal(@NonNull BigInteger n, String prefix, String postfix) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        boolean sign = n.signum() < 0;
        n = n.abs();
        String hexRepresentation = n.toString(16).toUpperCase();
        if (sign) {
            sb.append("-");
        }
        if ((hexRepresentation.length() % 2) != 0) {
            sb.append("0");
        }
        sb.append(hexRepresentation);
        if (postfix != null) {
            sb.append(postfix);
        }
        return sb.toString();
    }
}
