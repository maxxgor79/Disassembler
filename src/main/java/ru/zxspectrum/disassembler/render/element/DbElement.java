package ru.zxspectrum.disassembler.render.element;

import javax.print.attribute.standard.MediaSize;

/**
 * @author Maxim Gorin
 * Date: 02.03.2023
 */
public class DbElement extends TextElement {
    private static final String NAME = "DB";

    private int value;
    public DbElement(int value) {
        this.value = value;
        text = String.format("%s %d", NAME, value);
    }
}
