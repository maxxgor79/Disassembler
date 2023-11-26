package ru.zxspectrum.disassembler.command;

import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.io.PushbackDataInputStream;
import ru.zxspectrum.disassembler.render.element.CommandElement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
@Slf4j
public class GroupCommandDecompiler implements CommandDecompiler {
    private List<CommandDecompiler> commandDecompilers = new LinkedList<>();

    @Override
    public CommandElement decompile(PushbackDataInputStream dis) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandElement decompile(byte[] commandData) throws IOException {
        if (commandData == null) {
            throw new NullPointerException("commandData");
        }
        //TODO to add byteCodeOrder
        for (CommandDecompiler commandDecompiler : commandDecompilers) {
            try {
                 return commandDecompiler.decompile(new PushbackDataInputStream(new ByteArrayInputStream(commandData)));
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public boolean add(CommandDecompiler commandDecompiler) {
        if (commandDecompiler == null) {
            throw new NullPointerException("commandDecompiler");
        }
        return commandDecompilers.add(commandDecompiler);
    }

    @Override
    public int size() {
        return commandDecompilers.size();
    }
}
