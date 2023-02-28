package ru.zxspectrum.disassembler.render.element;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class LabelElement extends TextElement {
    public LabelElement(String s) {
        super(s);
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(getText()).append(":");
        return sb.toString();
    }


}
