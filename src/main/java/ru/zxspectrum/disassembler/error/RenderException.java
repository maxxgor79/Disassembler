package ru.zxspectrum.disassembler.error;

import java.io.File;

public class RenderException extends DisassemblerException {
    public RenderException() {
    }

    public RenderException(String message) {
        super(message);
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderException(Throwable cause) {
        super(cause);
    }

    public RenderException(int lineNumber, String message, Object... args) {
        super(lineNumber, message, args);
    }

    public RenderException(File file, int lineNumber, String message, Object... args) {
        super(file, lineNumber, message, args);
    }
}
