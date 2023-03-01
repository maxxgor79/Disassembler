package ru.zxspectrum.disassembler.command;

import ru.zxspectrum.disassembler.bytecode.ByteCodeCommandParser;
import ru.zxspectrum.disassembler.bytecode.ParamResult;
import ru.zxspectrum.disassembler.io.PushbackDataInputStream;
import ru.zxspectrum.disassembler.lang.Type;
import ru.zxspectrum.disassembler.render.element.CommandElement;
import ru.zxspectrum.disassembler.settings.Settings;
import ru.zxspectrum.disassembler.util.ConverterUtil;
import ru.zxspectrum.disassembler.util.TypeUtil;

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

    private Settings settings;

    private DecompilerNamespace decompilerNamespace;

    private int size = -1;

    public ParameterizedCommandDecompiler(PatternPair patternPair, Settings settings
            , DecompilerNamespace decompilerNamespace) {
        if (patternPair == null) {
            throw new NullPointerException("patternPair");
        }
        this.patternPair = patternPair;
        if (settings == null) {
            throw new NullPointerException("settings");
        }
        this.settings = settings;
        if (decompilerNamespace == null) {
            throw new NullPointerException("decompileNamespace");
        }
        this.decompilerNamespace = decompilerNamespace;
    }

    @Override
    public CommandElement decompile(PushbackDataInputStream dis) throws IOException {
        if (dis == null) {
            throw new NullPointerException("dis");
        }
        ByteCodeCommandParser parser = new ByteCodeCommandParser(patternPair.getCodePattern()
                , settings.getByteOrder());
        Collection<ParamResult> args = parser.parse(dis);
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
            ByteCodeCommandParser byteCodeCommandParser = new ByteCodeCommandParser(patternPair.getCodePattern()
                    , settings.getByteOrder());
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

    public String format(String mask, Collection<ParamResult> args) {
        if (mask == null) {
            return null;
        }
        if (args == null) {
            throw new NullPointerException("args");
        }
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(mask);
        scanner.useDelimiter("[$][ando]+");
        Iterator<ParamResult> iterator = args.iterator();
        while(scanner.hasNext()) {
            String s = scanner.next();
            sb.append(s);
            if (iterator.hasNext()) {
                ParamResult result  = iterator.next();
                sb.append(formatParameter(result));
            }
        }
        return sb.toString();
    }

    private String formatParameter(ParamResult result) {
        if (TypeUtil.isAddressOffsetPattern(result.getPatternParam())) {
            BigInteger finalAddress = decompilerNamespace.getAddress().add(result.getValue());
            return decompilerNamespace.addLabelAddress(finalAddress);
        } else {
            return ConverterUtil.toTextHexadecimal(result.getValue(), "$", null);
        }
    }
}
