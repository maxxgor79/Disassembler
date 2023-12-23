package ru.zxspectrum.disassembler.settings;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import ru.zxspectrum.disassembler.lang.ByteOrder;

import java.math.BigInteger;

/**
 * @author Maxim Gorin
 */
@Slf4j
public class DisassemblerSettings extends BaseSettings {
    public void load(@NonNull CommandLine cli) {
        // parse the command line arguments
        if (cli.hasOption("a")) {
            setDefaultAddress(new BigInteger(cli.getOptionValue("a")));
        }
        if (cli.hasOption("min")) {
            setMinAddress(new BigInteger(cli.getOptionValue("min")));
        }
        if (cli.hasOption("max")) {
            setMaxAddress(new BigInteger(cli.getOptionValue("max")));
        }
        if (cli.hasOption("o")) {
            setOutputDirectory(cli.getOptionValue("o"));
        }
        if (cli.hasOption("b")) {
            setByteOrder("big-endian".equals(cli.getOptionValue("b")) ? ByteOrder.BigEndian :
                    ByteOrder.LittleEndian);
        }
        if (cli.hasOption("v")) {
            setAddressVisible(true);
        }
    }

    @Override
    public void merge(@NonNull Settings settings) {
        setAddressVisible(settings.isAddressVisible());
        setAddressDimension(settings.getAddressDimension());
        if (getDefaultAddress() != null) {
            setDefaultAddress(getDefaultAddress());
        }
        if (getByteOrder() != null) {
            setByteOrder(getByteOrder());
        }
        if (getDestEncoding() != null) {
            setDestEncoding(getDestEncoding());
        }
        if (getCommentsTemplate() != null) {
            setCommentsTemplate(getCommentsTemplate());
        }
        if (getMajorVersion() != null) {
            setMajorVersion(getMajorVersion());
        }
        if (getMinorVersion() != null) {
            setMinorVersion(getMinorVersion());
        }
        if (getMaxAddress() != null) {
            setMaxAddress(getMaxAddress());
        }
        if (getMinAddress() != null) {
            setMinAddress(getMinAddress());
        }
        if (getCmdFilename() != null) {
            setCmdFilename(getCmdFilename());
        }
        if (getTemplates() != null) {
            setTemplates(getTemplates());
        }
    }
}
