package ru.zxspectrum.disassembler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.zxspectrum.disassembler.decompile.Decompiler;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.Output;
import ru.zxspectrum.disassembler.settings.DefaultSettings;
import ru.zxspectrum.disassembler.settings.DisassemblerSettings;
import ru.zxspectrum.disassembler.util.SymbolUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class Disassembler {
    private DisassemblerSettings settings;

    public Disassembler() {

    }

    public Disassembler(@NonNull DisassemblerSettings settings) {
        setSettings(settings);
    }

    protected void setSettings(@NonNull DisassemblerSettings settings) {
        this.settings = settings;
    }

    protected Collection<String> setCli(@NonNull final String[] args, @NonNull Options options) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cli = parser.parse(options, args);
            settings.load(cli);
            return cli.getArgList();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public void run(@NonNull File... files) throws IOException {
        Output.println(createWelcome());
        Decompiler decompiler = new Decompiler(settings);
        for (File file : files) {
            decompiler.decompile(file);
        }
    }

    private String createWelcome() {
        StringBuilder sb = new StringBuilder();
        String programWelcome = String.format(Messages.getMessage(Messages.PROGRAM_WELCOME), settings.getMajorVersion()
                , settings.getMinorVersion());
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
            final DisassemblerSettings settings = loadSettings();
            final Disassembler disassembler = new Disassembler(settings);
            Options options = getOptions();
            if (args.length == 0) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(settings.getCmdFilename() + " <file1>...<fileN>", options);
                return;
            }
            Collection<String> files = disassembler.setCli(args, options);
            if (files.isEmpty()) {
                Output.println("No input files");
                return;
            }
            for (String fileName : files) {
                disassembler.run(new File(fileName));
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }

    protected static DisassemblerSettings loadSettings() {
        final DisassemblerSettings settings = new DisassemblerSettings();
        settings.merge(new DefaultSettings());
        try {
            settings.load(Disassembler.class.getResourceAsStream("/settings.properties"));
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return settings;
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("a", "address", true, "default address." +
                " Non negative value.");
        options.addOption("min", "min-address", true, "minimal address." +
                " Non negative value.");
        options.addOption("max", "max-address", true, "maximal address." +
                " Non negative value.");
        options.addOption("o", "output", true, "output directory for" +
                " disassembled files.");
        options.addOption("b", "byte-order", true, "byte order" +
                ": little-endian or big-endian.");
        options.addOption("v", "visible", false, "Show or not address in decompiled file.");
        return options;
    }
}
