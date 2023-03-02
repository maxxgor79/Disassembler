package ru.zxspectrum.disassembler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.disassembler.decompile.Decompiler;
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
import java.util.Collections;
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

    private static File outputDirectory = new File("output");

    private static boolean visibleAddress;


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
            byteOrder = "big-endian".equals(value) ? ByteOrder.BigEndian : ByteOrder.LittleEndian;
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
            outputDirectory = new File(Variables.getString(Variables.OUTPUT, "output"));
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
            Disassembler disassembler = new Disassembler();
            Options options = getOptions();
            if (args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("disassembler <file1>...<fileN>", options);
                return;
            }
            List<String> fileList = cliParsing(args, options);
            for (String fileName : fileList) {
                disassembler.run(new File(fileName));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(e);
        }
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("a", "address", true, "default address." +
                " None negative value.");
        options.addOption("min", "min-address", true, "minimal address." +
                " None negative value.");
        options.addOption("max", "max-address", true, "maximal address." +
                " None negative value.");
        options.addOption("o", "output", true, "output directory for" +
                " disassembled files.");
        options.addOption("b", "byte-order", true, "byte order" +
                ": little-endian or big-endian.");
        options.addOption("v", "visible", false, "Show or not address in decompiled file.");
        return options;
    }

    private static List<String> cliParsing(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();

        try {
            // parse the command line arguments
            CommandLine cli = parser.parse(options, args);
            if (cli.hasOption("a")) {
                defaultAddress = new BigInteger(cli.getOptionValue("a"));
            }
            if (cli.hasOption("min")) {
                minAddress = new BigInteger(cli.getOptionValue("min"));
            }
            if (cli.hasOption("max")) {
                minAddress = new BigInteger(cli.getOptionValue("max"));
            }
            if (cli.hasOption("o")) {
                outputDirectory = new File(cli.getOptionValue("o"));
            }
            if (cli.hasOption("b")) {
                byteOrder = "big-endian".equals(cli.getOptionValue("b")) ? ByteOrder.BigEndian :
                        ByteOrder.LittleEndian;
            }
            if (cli.hasOption("v")) {
                visibleAddress = true;
            }
            return cli.getArgList();
        } catch (ParseException e) {
            logger.debug(e);
        }
        return Collections.emptyList();
    }

    public void run(File... files) throws IOException {
        Output.println(createWelcome());
        Decompiler decompiler = new Decompiler(this);
        for (File file : files) {
            decompiler.decompile(file);
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

    @Override
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public boolean isAddressVisible() {
        return visibleAddress;
    }
}
