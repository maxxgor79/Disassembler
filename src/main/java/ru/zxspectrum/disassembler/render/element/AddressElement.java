package ru.zxspectrum.disassembler.render.element;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 * Date: 01.03.2023
 */
public class AddressElement extends TextElement {
    private BigInteger address;

    private boolean visible = true;

    public AddressElement(BigInteger address, int dimension) {
        if (address == null) {
            throw new NullPointerException("address");
        }
        this.address = address;
        if (dimension <= 0) {
            throw new IllegalArgumentException("dimension <= 0");
        }
        text = String.format("$%0" + dimension + "X", address);
    }

    public BigInteger getAddress() {
        return this.address;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String generate() {
        return visible ? text : "";
    }
}
