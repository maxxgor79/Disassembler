package ru.zxspectrum.disassembler.error;

/**
 * @author Maxim Gorin
 * Date: 26.02.2023
 */
public class MatchException extends ParserException {
    public MatchException() {
    }

    public MatchException(String message) {
        super(message);
    }

    public MatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public MatchException(Throwable cause) {
        super(cause);
    }
}
