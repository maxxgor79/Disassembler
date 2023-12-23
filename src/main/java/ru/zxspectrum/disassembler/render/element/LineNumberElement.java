package ru.zxspectrum.disassembler.render.element;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
public class LineNumberElement implements Element {
    private BigInteger lineNumber;

    public LineNumberElement(BigInteger lineNumber) {
        if (lineNumber == null) {
            throw new NullPointerException("lineNumber");
        }
        this.lineNumber = lineNumber;
    }
    @Override
    public String generate() {
        return lineNumber.toString();
    }
}
