package ru.zxspectrum.disassembler.render.element;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2023
 */
public class SpaceElement extends TextElement {
    public static final SpaceElement SPACE = new SpaceElement();
    public SpaceElement() {
        super(" ");
    }
}
