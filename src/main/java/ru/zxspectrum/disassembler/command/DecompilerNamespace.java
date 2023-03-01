package ru.zxspectrum.disassembler.command;

import java.math.BigInteger;

/**
 * @Author: Maxim Gorin
 * Date: 28.02.2023
 */
public interface DecompilerNamespace {
    public BigInteger getAddress();

    public String addLabelAddress(BigInteger address);
}
