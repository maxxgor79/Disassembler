package ru.zxspectrum.disassembler.render.element;

/**
 * @author Maxim Gorin
 * Date: 27.02.2023
 */
public class CommandElement extends TextElement {
    private int byteCodeSize;

    public CommandElement(String s, int size) {
        super(s);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.byteCodeSize = size;
    }

    public int getByteCodeSize() {
        return byteCodeSize;
    }
}
