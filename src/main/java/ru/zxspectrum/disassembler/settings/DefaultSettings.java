package ru.zxspectrum.disassembler.settings;

import lombok.NonNull;
import ru.zxspectrum.disassembler.lang.ByteOrder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;

public class DefaultSettings implements Settings {
    protected static final ByteOrder BYTE_ORDER = ByteOrder.LittleEndian;

    protected static final int ADDRESS_DIMENSION = 4;

    protected static final String MINOR_VERSION = String.valueOf(1);

    protected static final String MAJOR_VERSION = String.valueOf(1);

    protected static final String DEST_ENCODING = Charset.defaultCharset().name();

    protected static final BigInteger DEFAULT_ADDRESS = new BigInteger("8000", 16);

    protected static final BigInteger MIN_ADDRESS = BigInteger.ZERO;

    protected static final BigInteger MAX_ADDRESS = new BigInteger("ffff", 16);

    protected static final String COMMENTS_TEMPLATE = "";

    protected static final String OUTPUT_DIRECTORY = "output";

    protected static final boolean ADDRESS_VISIBLE = true;

    protected static final String CMD_FILENAME = "disasm";

    protected static final Collection<String> TEMPLATES = Collections.emptyList();


    @Override
    public ByteOrder getByteOrder() {
        return BYTE_ORDER;
    }

    @Override
    public int getAddressDimension() {
        return ADDRESS_DIMENSION;
    }

    @Override
    public String getMinorVersion() {
        return MINOR_VERSION;
    }

    @Override
    public String getMajorVersion() {
        return MAJOR_VERSION;
    }

    @Override
    public String getDestEncoding() {
        return DEST_ENCODING;
    }

    @Override
    public BigInteger getDefaultAddress() {
        return DEFAULT_ADDRESS;
    }

    @Override
    public BigInteger getMinAddress() {
        return MIN_ADDRESS;
    }

    @Override
    public BigInteger getMaxAddress() {
        return MAX_ADDRESS;
    }

    @Override
    public String getCommentsTemplate() {
        return COMMENTS_TEMPLATE;
    }

    @Override
    public Collection<String> getTemplates() {
        return null;
    }

    @Override
    public File getOutputDirectory() {
        return new File(OUTPUT_DIRECTORY);
    }

    @Override
    public boolean isAddressVisible() {
        return ADDRESS_VISIBLE;
    }

    @Override
    public String getCmdFilename() {
        return CMD_FILENAME;
    }

    @Override
    public void load(@NonNull InputStream is) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge(@NonNull Settings settings) {
        throw new UnsupportedOperationException();
    }
}
