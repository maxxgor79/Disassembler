package ru.zxspectrum.disassembler.bytecode;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 * Date: 01.03.2023
 */
public class ParamResult {
    private BigInteger value;

    private String patternParam;

    public ParamResult(BigInteger value, String patternParam) {
        setValue(value);
        setPatternParam(patternParam);
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    public String getPatternParam() {
        return patternParam;
    }

    public void setPatternParam(String patternParam) {
        if (patternParam == null) {
            throw new NullPointerException("patternParam");
        }
        this.patternParam = patternParam;
    }
}
