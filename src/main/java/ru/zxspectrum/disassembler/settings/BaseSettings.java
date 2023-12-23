package ru.zxspectrum.disassembler.settings;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.lang.ByteOrder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class BaseSettings implements Settings {
    @Setter(AccessLevel.PROTECTED)
    private ByteOrder byteOrder;

    @Setter(AccessLevel.PROTECTED)
    private int addressDimension;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String minorVersion;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String majorVersion;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String destEncoding;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private BigInteger defaultAddress;

    @Setter(AccessLevel.PACKAGE)
    @NonNull
    private BigInteger minAddress;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private BigInteger maxAddress;

    @Setter(AccessLevel.PACKAGE)
    private String commentsTemplate;

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String outputDirectory;

    @Setter(AccessLevel.PROTECTED)
    private boolean addressVisible;

    private final Collection<String> templates = new LinkedList<>();

    @Setter(AccessLevel.PROTECTED)
    @NonNull
    private String cmdFilename;

    public BaseSettings() {

    }

    @Override
    public void load(@NonNull InputStream is) throws IOException {
        Variables.load(is);
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
        cmdFilename = Variables.getString(Variables.CMD_FILENAME, "disasm");
        for (int i = 0; i < 256; i++) {
            value = Variables.getString(Variables.TEMPLATE + i, null);
            if (value == null) {
                break;
            }
            templates.add(value);
        }
        outputDirectory = Variables.getString(Variables.OUTPUT_DIRECTORY, "output");
    }

    @Override
    public void merge(@NonNull Settings settings) {
        throw new UnsupportedOperationException();
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
        return Collections.unmodifiableCollection(templates);
    }

    @Override
    public File getOutputDirectory() {
        return new File(outputDirectory);
    }

    @Override
    public boolean isAddressVisible() {
        return addressVisible;
    }

    @Override
    public String getCmdFilename() {
        return cmdFilename;
    }

    protected void setTemplates(@NonNull Collection<String> templates) {
        this.templates.addAll(templates);
    }
}
