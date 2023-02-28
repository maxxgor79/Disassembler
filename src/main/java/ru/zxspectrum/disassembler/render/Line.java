package ru.zxspectrum.disassembler.render;

import ru.zxspectrum.disassembler.render.element.Element;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Line implements Render {
    private List<Element> elementList = new LinkedList<>();

    public Line(Element ... elements) {
        if (elements == null) {
            throw new NullPointerException("elements");
        }
        elementList.addAll(Arrays.asList(elements));
    }


    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Element e : elementList) {
            sb.append(e.generate());
            i++;
            if (i < elementList.size()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
