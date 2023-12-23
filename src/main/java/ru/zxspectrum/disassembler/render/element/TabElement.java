package ru.zxspectrum.disassembler.render.element;

/**
 * @author Maxim Gorin
 * Date: 01.03.2023
 */
public class TabElement extends TextElement {
    public static final TabElement TAB = new TabElement();

    public TabElement() {
        super("\t");
    }
}
