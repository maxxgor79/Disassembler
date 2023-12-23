package ru.zxspectrum.disassembler.command;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
 * @author Maxim Gorin
 * Date: 25.02.2023
 */
@Slf4j
public class ParameterizedCommandDecompiler implements CommandDecompiler {
    private PatternPair patternPair;

    private Settings settings;

    private DecompilerNamespace decompilerNamespace;

    private int size = -1;

    public ParameterizedCommandDecompiler(@NonNull PatternPair patternPair, @NonNull Settings settings
            , @NonNull DecompilerNamespace decompilerNamespace) {
        this.patternPair = patternPair;
        this.settings = settings;
        this.decompilerNamespace = decompilerNamespace;
    }

    @Override
    public CommandElement decompile(@NonNull PushbackDataInputStream dis) throws IOException {
        ByteCodeCommandParser parser = new ByteCodeCommandParser(patternPair.getCodePattern()
                , settings.getByteOrder());
        Collection<ParamResult> args = parser.parse(dis);
        return new CommandElement(render(args), size);
    }

    @Override
    public CommandElement decompile(byte[] commandData) throws IOException {
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
                        size += type.getSize();
                    } else {
                        break;
                    }
                }
            }
            this.size = size;
        }
        return size;
    }

    private String render(@NonNull Collection<ParamResult> params) {
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
