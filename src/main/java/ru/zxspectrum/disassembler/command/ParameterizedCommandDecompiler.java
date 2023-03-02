package ru.zxspectrum.disassembler.command;

import ru.zxspectrum.disassembler.bytecode.ByteCodeCommandParser;
import ru.zxspectrum.disassembler.bytecode.ParamResult;
import ru.zxspectrum.disassembler.decompile.DecompilerNamespace;
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
        return new CommandElement(render(args), size);
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

    private String render(Collection<ParamResult> params) {
        if (params == null) {
            throw new NullPointerException("params");
        }
        String mask = patternPair.getCommandPattern();
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(mask);
        scanner.useDelimiter("[$][ando]+");
        Iterator<ParamResult> iterator = params.iterator();
        while(scanner.hasNext()) {
            String s = scanner.next();
            sb.append(s);
            if (iterator.hasNext()) {
                ParamResult param  = iterator.next();
                renderParameter(sb, param, params);
            }
        }
        return sb.toString();
    }

    private void renderParameter(StringBuilder sb, ParamResult param, Collection<ParamResult> params) {
        BigInteger value = param.getValue();
        if (TypeUtil.isAddressOffsetPattern(param.getPatternParam())) {
            value = decompilerNamespace.getAddress().add(param.getValue());//relative address
            decompilerNamespace.addLabelAddress(value, true);
            decompilerNamespace.addRequestedLabel(decompilerNamespace.getAddress(), patternPair.getCommandPattern()
                    , params);
        } else {
            if (TypeUtil.isAddressPattern(param.getPatternParam())) {
                decompilerNamespace.addLabelAddress(value, false);//absolute address
                decompilerNamespace.addRequestedLabel(decompilerNamespace.getAddress(), patternPair.getCommandPattern()
                        , params);
            }
        }
        sb.append(ConverterUtil.toTextHexadecimal(value, "$", null));
    }
}
