package ru.zxspectrum.disassembler.render.element;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2023
 */
public class EOLElement extends TextElement {
    public static final EOLElement EOL = new EOLElement();
    public EOLElement() {
        super(System.lineSeparator());
    }
}
