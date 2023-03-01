package ru.zxspectrum.disassembler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.disassembler.command.Decompiler;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.Output;
import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.settings.Settings;
import ru.zxspectrum.disassembler.util.SymbolUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Disassembler implements Settings {
    private static final Logger logger = LogManager.getLogger(Disassembler.class.getName());

    private static String majorVersion = "1";

    private static String minorVersion = "0";

    private static ByteOrder byteOrder = ByteOrder.LittleEndian;

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
        try {
            if (args.length == 0) {
                System.out.println("Usage: disassembler <file1>...<fileN");
                return;
            }
            Disassembler disassembler = new Disassembler();
            List<File> fileList = new LinkedList<>();
            for (String fileName : args) {
                fileList.add(new File(fileName));
            }
            disassembler.run(fileList.toArray(new File[fileList.size()]));
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e);
        }
     }

     public void run(File ... files) throws IOException {
         Output.println(createWelcome());
         Decompiler decompiler = new Decompiler(this);
         for (File file : files) {
             String s = decompiler.decompile(file);
             System.out.print(s);
         }
     }

    @Override
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public int getAddressDimension() {
        return 4;
    }
}
