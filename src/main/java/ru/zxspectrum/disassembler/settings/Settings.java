package ru.zxspectrum.disassembler.settings;

import ru.zxspectrum.disassembler.lang.ByteOrder;

/**
 * @Author: Maxim Gorin
 * Date: 28.02.2023
 */
public interface Settings {
    public ByteOrder getByteOrder();

    public int getAddressDimension();


}
