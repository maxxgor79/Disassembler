package ru.zxspectrum.disassembler;

import ru.zxspectrum.disassembler.command.Decompiler;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.Output;
import ru.zxspectrum.disassembler.util.SymbolUtil;

import java.io.File;
import java.io.IOException;

public class Disassembler {
    private static String majorVersion = "1";

    private static String minorVersion = "0";

    private static String createWelcome() {
        StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(Messages.getMessage(Messages.PROGRAM_WELCOME), majorVersion
                , minorVersion);
        String writtenBy = Messages.getMessage(Messages.WRITTEN_BY);
        String lineExternal = SymbolUtil.fillChar('*', 80);
        sb.append(lineExternal).append(System.lineSeparator());
        String lineInternal = (new StringBuilder().append('*').append(SymbolUtil.fillChar(' ', 78))
                .append('*')).toString();
        sb.append(SymbolUtil.replace(lineInternal, (lineInternal.length() - programWelcome.length()) / 2
                , programWelcome)).append(System.lineSeparator());
        sb.append(SymbolUtil.replace(lineInternal, (lineInternal.length() - writtenBy.length()) / 2
                , writtenBy)).append(System.lineSeparator());
        sb.append(lineExternal).append(System.lineSeparator());
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        Output.println(createWelcome());
        Decompiler decompiler = new Decompiler();
        decompiler.decompile(new File(args[0]));
     }
}
