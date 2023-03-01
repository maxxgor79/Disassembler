package ru.zxspectrum.disassembler.settings;

import ru.zxspectrum.disassembler.lang.ByteOrder;

import java.math.BigInteger;
import java.util.Collection;

/**
 * @Author: Maxim Gorin
 * Date: 28.02.2023
 */
public interface Settings {
    public ByteOrder getByteOrder();

    public int getAddressDimension();

    public String getMinorVersion();

    public String getMajorVersion();

    public String getDestEncoding();

    public BigInteger getDefaultAddress();

    public BigInteger getMinAddress();

    public BigInteger getMaxAddress();

    public String getCommentsTemplate();

    public Collection<String> getTemplates();
}
