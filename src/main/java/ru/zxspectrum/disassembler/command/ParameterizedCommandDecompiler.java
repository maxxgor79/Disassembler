package ru.zxspectrum.disassembler.command;

import ru.zxspectrum.disassembler.bytecode.ByteCodeCommandParser;
import ru.zxspectrum.disassembler.io.PushbackDataInputStream;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.render.element.CommandElement;
import ru.zxspectrum.disassembler.util.ConverterUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class ParameterizedCommandDecompiler implements CommandDecompiler {
    private PatternPair patternPair;

    private int size = -1;

    public ParameterizedCommandDecompiler(PatternPair patternPair) {
        if (patternPair == null) {
            throw new NullPointerException("patternPair");
        }
        this.patternPair = patternPair;
    }

    @Override
    public CommandElement decompile(PushbackDataInputStream dis) throws IOException {
        if (dis == null) {
            throw new NullPointerException("dis");
        }
        ByteCodeCommandParser parser = new ByteCodeCommandParser(patternPair.getCodePattern());
        Collection<BigInteger> args = parser.parse(dis);
        return new CommandElement(format(patternPair.getCommandPattern(), args), size);
    }

    @Override
    public CommandElement decompile(byte[] commandData) throws IOException {
        if (commandData == null) {
            throw new NullPointerException("commandData");
        }
        return decompile(new PushbackDataInputStream(new ByteArrayInputStream(commandData)));
    }

    @Override
    public int size() {
        if (size < 0) {
            ByteCodeCommandParser byteCodeCommandParser = new ByteCodeCommandParser(patternPair.getCodePattern());
            int size = 0;
            while (true) {
                if (byteCodeCommandParser.isNextByte()) {
                    byteCodeCommandParser.readUnsignedByte();
                    size++;
                } else {
                    if (byteCodeCommandParser.isNextParam()) {
                        Type type = byteCodeCommandParser.readParamType();
                        size += Type.sizeOf(type);
                    } else {
                        break;
                    }
                }
            }
            this.size = size;
        }
        return size;
    }

    public static String format(String mask, Collection<BigInteger> args) {
        if (mask == null) {
            return null;
        }
        if (args == null) {
            throw new NullPointerException("args");
        }
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(mask);
        scanner.useDelimiter("[$][ando]+");
        Iterator<BigInteger> iterator = args.iterator();
        while(scanner.hasNext()) {
            String s = scanner.next();
            sb.append(s);
            if (iterator.hasNext()) {
                sb.append(ConverterUtil.toTextHexadecimal(iterator.next(), "$", null));
            }
        }
        return sb.toString();
    }
}
