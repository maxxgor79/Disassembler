package ru.zxspectrum.disassembler.error;

import ru.zxspectrum.disassembler.io.Output;

import java.io.File;

public class DisassemblerException extends RuntimeException {

    public DisassemblerException() {
    }

    public DisassemblerException(String message) {
        super(message);
    }

    public DisassemblerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DisassemblerException(Throwable cause) {
        super(cause);
    }

    public DisassemblerException(int lineNumber, String message, Object ... args) {
        this(Output.errorFormat(lineNumber, message, args));
    }

    public DisassemblerException(File file, int lineNumber, String message, Object ... args) {
        this(Output.errorFormat(file.getAbsolutePath(), lineNumber, message, args));
    }
}
