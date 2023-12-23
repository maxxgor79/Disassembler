package ru.zxspectrum.disassembler.command;

import ru.zxspectrum.disassembler.io.PushbackDataInputStream;
import ru.zxspectrum.disassembler.render.element.CommandElement;

import java.io.IOException;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
public interface CommandDecompiler {
    public CommandElement decompile(PushbackDataInputStream is) throws IOException;

    public CommandElement decompile(byte []commandData) throws IOException;

    public int size();
}
