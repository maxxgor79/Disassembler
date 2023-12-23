package ru.zxspectrum.disassembler.bytecode;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.error.MatchException;
import ru.zxspectrum.disassembler.error.ParserException;
import ru.zxspectrum.disassembler.io.PushbackDataInputStream;
import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.util.SymbolUtil;
import ru.zxspectrum.disassembler.util.TypeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
@Slf4j
public class ByteCodeCommandParser {
    private String codePattern;

    private ByteOrder byteOrder;

    private int curIndex;

    public ByteCodeCommandParser(String codePattern) {
        this(codePattern, ByteOrder.LittleEndian);
    }

    public ByteCodeCommandParser(String codePattern, ByteOrder byteOrder) {
        if (codePattern == null) {
            throw new NullPointerException("codePattern");
        }
        this.codePattern = codePattern;
        if (byteOrder == null) {
            byteOrder = ByteOrder.LittleEndian;
        }
        this.byteOrder = byteOrder;
    }

    public Collection<ParamResult> parse(InputStream in) throws IOException {
        return parse(new PushbackDataInputStream(in, byteOrder));
    }

    public Collection<ParamResult> parse(@NonNull PushbackDataInputStream dis) throws IOException {
        List<ParamResult> paramList = new LinkedList<>();
        while (true) {
            if (isNextByte()) {
                int b = readUnsignedByte();
                int b1 = dis.readUnsignedByte();
                if (b != b1) {
                    throw new MatchException("b=" + b + ",b1=" + b1);
                }
            } else {
                if (isNextParam()) {
                    String paramPattern = readParamPattern();
                    Type type = TypeUtil.getParamPatternType(paramPattern);
                    if (type == Type.Unknown) {
                        throw new ParserException("Unknown type:");
                    }
                    paramList.add(new ParamResult(dis.read(type), paramPattern));
                } else {
                    break;
                }
            }
        }
        return paramList;
    }

    public boolean isNextByte() {
        if (curIndex >= codePattern.length()) {
            return false;
        }
        int ch = codePattern.charAt(curIndex);
        return SymbolUtil.isHexadecimalDigit(ch);
    }

    public boolean isNextParam() {
        if (curIndex >= codePattern.length()) {
            return false;
        }
        int ch = codePattern.charAt(curIndex);
        return SymbolUtil.isDollar(ch);
    }

    protected int read() {
        if (curIndex >= codePattern.length()) {
            return -1;
        }
        return codePattern.charAt(curIndex++);
    }

    public int readUnsignedByte() {
        int ch1 = read();
        int ch2 = read();
        if (ch1 == -1 || ch2 == -1) {
            throw new ParserException("Bad format");
        }
        if (!SymbolUtil.isHexadecimalDigit(ch1) || !SymbolUtil.isHexadecimalDigit(ch2)) {
            throw new ParserException("Bad number format");
        }
        return Integer.parseInt(String.format("%c%c", (char) ch1, (char) ch2), 16);
    }

    public String readParamPattern() {
        StringBuilder sb = new StringBuilder();
        int ch = read();
        if (!SymbolUtil.isDollar(ch)) {
            throw new ParserException("'$' expected");
        }
        while ((ch = read()) != -1) {
            if (TypeUtil.isAddressPatternSymbol(ch) || TypeUtil.isAddressOffsetPatternSymbol(ch)
                    || TypeUtil.isNumberPattenSymbol(ch) || TypeUtil.isOffsetPatternSymbol(ch)) {
                sb.append((char) ch);
            } else {
                pushback();
                break;
            }
        }
        if (sb.length() == 0) {
            throw new ParserException("parameter is empty");
        }
        return sb.toString();
    }

    public Type readParamType() {
        return TypeUtil.getParamPatternType(readParamPattern());
    }

    private void pushback() {
        if (curIndex > 0) {
            curIndex--;
        }
    }
}
