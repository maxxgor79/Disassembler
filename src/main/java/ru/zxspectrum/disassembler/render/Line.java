package ru.zxspectrum.disassembler.render;

import lombok.NonNull;
import ru.zxspectrum.disassembler.render.element.AddressElement;
import ru.zxspectrum.disassembler.render.element.EOLElement;
import ru.zxspectrum.disassembler.render.element.Element;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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

    public Line(AddressElement addressElement, @NonNull Collection<Element> elements) {
        this.addressElement = addressElement;
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

    public Collection<Element> replace(Class<? extends Element> clazz, Element newElement) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        if (newElement == null) {
            throw new NullPointerException("newElement");
        }
        ListIterator<Element> listIterator = elementList.listIterator(0);
        List<Element> removedList = new LinkedList<>();
        while(listIterator.hasNext()) {
            Element e = listIterator.next();
            if (e.getClass() == clazz) {
                removedList.add(e);
                listIterator.set(newElement);
            }
        }
        return removedList;
    }
}
