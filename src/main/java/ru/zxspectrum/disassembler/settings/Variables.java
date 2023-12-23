package ru.zxspectrum.disassembler.settings;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * @author Maxim Gorin
 * Date: 01.03.2023
 */
public final class Variables {
    private Variables() {

    }

    public static final String DEFAULT_ADDRESS = "default_address";

    public static final String MIN_ADDRESS = "min_address";

    public static final String MAX_ADDRESS = "max_address";

    public static final String BYTE_ORDER = "byte_order";

    public static final String DEST_ENCODING = "dest_encoding";

    public static final String MAJOR_VERSION = "major_version";

    public static final String MINOR_VERSION = "minor_version";

    public static final String ADDRESS_DIMENSION = "address_dimension";

    public static final String TEMPLATE = "template";

    public static final String COMMENT_TEMPLATE = "comment_template";

    public static final String OUTPUT_DIRECTORY = "output_directory";

    public static final String CMD_FILENAME = "cmd_filename";


    public static int getInt(String name, int defaultValue) {
        if (name == null) {
            return defaultValue;
        }
        String value = System.getProperty(name);
        if (value == null) {
            return defaultValue;
        }
        int radix = 10;
        if (value.toLowerCase().endsWith("h")) {
            value = value.substring(0, value.length() - 1);
            radix = 16;
        }
        if (value.toLowerCase().startsWith("0x")) {
            value = value.substring(2, value.length());
            radix = 16;
        }
        if (value.toLowerCase().endsWith("b")) {
            value = value.substring(0, value.length() - 1);
            radix = 2;
        }
        try {
            return Integer.parseInt(value, radix);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static BigInteger getBigInteger(String name, BigInteger def) {
        if (name == null) {
            return def;
        }
        String value = System.getProperty(name);
        if (value == null) {
            return def;
        }
        int radix = 10;
        if (value.toLowerCase().endsWith("h")) {
            value = value.substring(0, value.length() - 1);
            radix = 16;
        }
        if (value.toLowerCase().startsWith("0x")) {
            value = value.substring(2, value.length());
            radix = 16;
        }
        if (value.toLowerCase().endsWith("b")) {
            value = value.substring(0, value.length() - 1);
            radix = 2;
        }
        try {
            return new BigInteger(value, radix);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getString(String name, String def) {
        return System.getProperty(name, def);
    }

    public static void setString(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        System.setProperty(name, value);
    }

    public static void load(InputStream is) throws IOException {
        if (is == null) {
            throw new NullPointerException("is");
        }
        System.getProperties().load(is);
    }

}
