package ru.zxspectrum.disassembler.decompile;

import ru.zxspectrum.disassembler.bytecode.ParamResult;

import java.math.BigInteger;
import java.util.Collection;

/**
 * @Author: Maxim Gorin
 * Date: 28.02.2023
 */
public interface DecompilerNamespace {
    BigInteger getAddress();

    String addLabelAddress(BigInteger address, boolean required);

    default public String addLabelAddress(BigInteger address) {
        return addLabelAddress(address, false);
    }

    void addRequestedLabel(BigInteger address, String mask, Collection<ParamResult> params);

    String getLabel(BigInteger address);
}
