package ru.zxspectrum.disassembler.render.element;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class TextElement implements Element {
    private String text;

    public TextElement(String s) {
        if (s == null) {
            throw new NullPointerException("s");
        }
        this.text = s;
    }
    @Override
    public String generate() {
        return text;
    }

    public String getText() {
        return text;
    }
}
