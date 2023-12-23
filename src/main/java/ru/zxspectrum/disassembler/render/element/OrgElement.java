package ru.zxspectrum.disassembler.render.element;

import ru.zxspectrum.disassembler.settings.Settings;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 * Date: 01.03.2023
 */
public class OrgElement extends TextElement {
    private static final String HEADER = "ORG";
    private AddressElement addressElement;

    public OrgElement(BigInteger address, int dimension) {
        addressElement = new AddressElement(address, dimension);
        StringBuilder sb = new StringBuilder();
        text = sb.append(HEADER).append(TabElement.TAB.generate()).append(TabElement.TAB.generate())
                .append(addressElement.generate()).toString();
    }
}
