package ru.zxspectrum.disassembler.error;

import java.io.File;

/**
 * @author Maxim Gorin
 * Date: 27.02.2023
 */
public class DecompilerException extends DisassemblerException {
    public DecompilerException() {
    }

    public DecompilerException(String message) {
        super(message);
    }

    public DecompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecompilerException(Throwable cause) {
        super(cause);
    }

    public DecompilerException(int lineNumber, String message, Object... args) {
        super(lineNumber, message, args);
    }

    public DecompilerException(File file, int lineNumber, String message, Object... args) {
        super(file, lineNumber, message, args);
    }
}
