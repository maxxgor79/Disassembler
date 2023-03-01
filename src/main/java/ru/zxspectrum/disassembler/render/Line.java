package ru.zxspectrum.disassembler.render;

import ru.zxspectrum.disassembler.render.element.AddressElement;
import ru.zxspectrum.disassembler.render.element.EOLElement;
import ru.zxspectrum.disassembler.render.element.Element;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class Line implements Render {

    private AddressElement addressElement;

    private LinkedList<Element> elementList = new LinkedList<>();

    public Line(AddressElement addressElement, Element ... elements) {
        this.addressElement = addressElement;
        if (elements == null) {
            throw new NullPointerException("elements");
        }
        elementList.addAll(Arrays.asList(elements));
    }

    public Line(AddressElement addressElement, Collection<Element> elements) {
        this.addressElement = addressElement;
        if (elements == null) {
            throw new NullPointerException("elements");
        }
        elementList.addAll(elements);
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        if (addressElement != null) {
            sb.append(addressElement.generate());
        }
        for (Element e : elementList) {
            sb.append(e.generate());
        }
        sb.append(EOLElement.EOL.generate());
        return sb.toString();
    }

    public AddressElement getAddressElement() {
        return addressElement;
    }

    public Element getFirst() {
        return elementList.getFirst();
    }

    public Element getLast() {
        return elementList.getLast();
    }
}
