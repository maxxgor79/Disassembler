package ru.zxspectrum.disassembler.error;

import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class ParserException extends DisassemblerException {
    public ParserException() {
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(int lineNumber, String message, Object... args) {
        super(lineNumber, message, args);
    }

    public ParserException(File file, int lineNumber, String message, Object... args) {
        super(file, lineNumber, message, args);
    }
}
