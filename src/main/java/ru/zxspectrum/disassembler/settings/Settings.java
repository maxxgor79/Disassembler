package ru.zxspectrum.disassembler.settings;

import ru.zxspectrum.disassembler.lang.ByteOrder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;

/**
 * @Author: Maxim Gorin
 * Date: 28.02.2023
 */
public interface Settings {
    ByteOrder getByteOrder();

    int getAddressDimension();

    String getMinorVersion();

    String getMajorVersion();

    String getDestEncoding();

    BigInteger getDefaultAddress();

    BigInteger getMinAddress();

    BigInteger getMaxAddress();

    String getCommentsTemplate();

    Collection<String> getTemplates();

    File getOutputDirectory();

    boolean isAddressVisible();

    String getCmdFilename();

    void load(InputStream is) throws IOException;

    void merge(Settings settings);
}
