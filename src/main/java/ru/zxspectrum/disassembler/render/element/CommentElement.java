package ru.zxspectrum.disassembler.render.element;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
public class CommentElement extends TextElement{
    public CommentElement(String s) {
        super(s);
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        sb.append(';').append(getText());
        return sb.toString();
    }
}
