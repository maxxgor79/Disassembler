package ru.zxspectrum.disassembler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.disassembler.command.Decompiler;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.Output;
import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.settings.Settings;
import ru.zxspectrum.disassembler.settings.Variables;
import ru.zxspectrum.disassembler.util.SymbolUtil;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Disassembler implements Settings {
    private static final Logger logger = LogManager.getLogger(Disassembler.class.getName());

    private static String majorVersion = "1";

    private static String minorVersion = "0";

    private static String destEncoding = Charset.defaultCharset().name();

    private static ByteOrder byteOrder = ByteOrder.LittleEndian;

    private static BigInteger defaultAddress = BigInteger.ZERO;

    private static BigInteger minAddress = BigInteger.ZERO;

    private static BigInteger maxAddress = BigInteger.valueOf(65535);

    private static String commentsTemplate = "";

    private static List<String> templateList = new LinkedList<>();

    private static int addressDimension = 4;



    public Disassembler() {
        loadSettings();
    }

    private void loadSettings() {
        try {
            Variables.load(Disassembler.class.getResourceAsStream("/settings.properties"));
            majorVersion = Variables.getString(Variables.MAJOR_VERSION, majorVersion);
            minorVersion = Variables.getString(Variables.MINOR_VERSION, minorVersion);
            destEncoding = Variables.getString(Variables.DEST_ENCODING, destEncoding);
            String value = Variables.getString(Variables.BYTE_ORDER, "little-endian");
            if ("big-endian".equals(value)) {
                byteOrder = ByteOrder.BigEndian;
            } else {
                byteOrder = ByteOrder.LittleEndian;
            }
            defaultAddress = Variables.getBigInteger(Variables.DEFAULT_ADDRESS, defaultAddress);
            minAddress = Variables.getBigInteger(Variables.MIN_ADDRESS, minAddress);
            maxAddress = Variables.getBigInteger(Variables.MAX_ADDRESS, maxAddress);
            commentsTemplate = Variables.getString(Variables.COMMENT_TEMPLATE, commentsTemplate);
            addressDimension = Variables.getInt(Variables.ADDRESS_DIMENSION, addressDimension);
            for (int i = 0; i < 32; i++) {
                value = Variables.getString(Variables.TEMPLATE + i, null);
                if (value == null) {
                    break;
                }
                templateList.add(value);
            }
        } catch (Exception e) {
            logger.debug(e);
        }
    }

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
        return addressDimension;
    }

    @Override
    public String getMinorVersion() {
        return minorVersion;
    }

    @Override
    public String getMajorVersion() {
        return majorVersion;
    }

    @Override
    public String getDestEncoding() {
        return destEncoding;
    }

    @Override
    public BigInteger getDefaultAddress() {
        return defaultAddress;
    }

    @Override
    public BigInteger getMinAddress() {
        return minAddress;
    }

    @Override
    public BigInteger getMaxAddress() {
        return maxAddress;
    }

    @Override
    public String getCommentsTemplate() {
        return commentsTemplate;
    }

    @Override
    public Collection<String> getTemplates() {
        return templateList;
    }
}
