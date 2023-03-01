package ru.zxspectrum.disassembler.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.disassembler.error.DecompilerException;
import ru.zxspectrum.disassembler.io.PatternLoader;
import ru.zxspectrum.disassembler.io.PushbackInputStream;
import ru.zxspectrum.disassembler.render.ElementList;
import ru.zxspectrum.disassembler.render.Line;
import ru.zxspectrum.disassembler.render.element.AddressElement;
import ru.zxspectrum.disassembler.render.element.CommandElement;
import ru.zxspectrum.disassembler.render.element.LabelElement;
import ru.zxspectrum.disassembler.render.element.OrgElement;
import ru.zxspectrum.disassembler.render.element.TabElement;
import ru.zxspectrum.disassembler.settings.Settings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
public class Decompiler implements DecompilerNamespace {
    private static final Logger logger = LogManager.getLogger(Decompiler.class.getName());

    private Settings settings;
    private CommandDecompilerTable commandDecompilerTable;

    private BigInteger address = BigInteger.ZERO;

    private Map<BigInteger, String> labelMap = new HashMap<>();

    private ElementList elementList = new ElementList();

    private static int labelIndex;

    public Decompiler(Settings settings) {
        if (settings == null) {
            throw new NullPointerException("settings");
        }
        this.settings = settings;
        try {
            loadCommandDecompilerTables();
        } catch (IOException e) {
            logger.debug(e);
        }
    }

    private Collection<PatternPair> loadCommandDecompilerTable(InputStream is) throws IOException {
        PatternLoader loader = new PatternLoader();
        return loader.load(is);
    }

    private void loadCommandDecompilerTables() throws IOException {
        if (commandDecompilerTable == null) {
            commandDecompilerTable = new CommandDecompilerTable(settings, this);
            commandDecompilerTable.putAll(loadCommandDecompilerTable(Decompiler.class
                    .getResourceAsStream("/template/z80/general.op")));
            commandDecompilerTable.putAll(loadCommandDecompilerTable(Decompiler.class
                    .getResourceAsStream("/template/z80/extended.op")));
            commandDecompilerTable.putAll(loadCommandDecompilerTable(Decompiler.class
                    .getResourceAsStream("/template/z80/bitwise.op")));
            commandDecompilerTable.putAll(loadCommandDecompilerTable(Decompiler.class
                    .getResourceAsStream("/template/z80/ix.op")));
            commandDecompilerTable.putAll(loadCommandDecompilerTable(Decompiler.class
                    .getResourceAsStream("/template/z80/iy.op")));
        }
    }

    public String decompile(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file");

        }
        if (this.commandDecompilerTable.size() == 0) {
            throw new DecompilerException("Command table is empty");
        }
        resetSettings();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            PushbackInputStream pis = new PushbackInputStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] commandData = new byte[commandDecompilerTable.getMaxCommandSize()];
            addFirstLine();
            while (true) {
                final int readBytes = pis.read(commandData);
                if (readBytes == -1) {
                    break;
                }
                baos.reset();
                int i = 0;
                for (; i < readBytes; i++) {
                    baos.write(commandData[i]);
                    CommandElement commandElement = commandDecompilerTable.generate(baos.toByteArray(), commandData);
                    if (commandElement != null) {
                        byte[] data = Arrays.copyOfRange(commandData, commandElement.getByteCodeSize()
                                , readBytes);
                        pis.pushback(data);
                        addLine(getAddress(), commandElement);
                        incrementAddress(commandElement.getByteCodeSize());
                        break;
                    }
                }
                if (i >= readBytes) {
                    throw new DecompilerException("Command is not found");
                }
            }
            postDecompile();
            return elementList.generate();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    logger.debug(e);
                }
            }
        }

    }

    private void resetSettings() {
        labelMap.clear();
        elementList.clear();
        labelIndex = 0;
    }

    private void addFirstLine() {
        OrgElement orgElement = new OrgElement(getAddress(), settings.getAddressDimension());
        elementList.add(new Line(null, orgElement));
    }

    private void addLine(BigInteger address, CommandElement commandElement) {
        AddressElement addressElement = new AddressElement(address, settings.getAddressDimension());
        Line line = new Line(addressElement, TabElement.TAB, TabElement.TAB, commandElement);
        elementList.add(line);
    }


    @Override
    public BigInteger getAddress() {
        return this.address;
    }

    @Override
    public String addLabelAddress(BigInteger address) {
        if (address == null) {
            throw new NullPointerException("address");
        }
        String name = labelMap.get(address);
        if (name != null) {
            return name;
        }
        name = generateLabelName();
        labelMap.put(address, name);
        return name;
    }

    private static String generateLabelName() {
        String name = String.format("label%06d", labelIndex++);
        return name;
    }

    protected void setAddress(BigInteger address) {
        if (address == null) {
            throw new NullPointerException("address");
        }
        this.address = address;
    }

    protected BigInteger incrementAddress(int d) {
        if (d < 0) {
            throw new IllegalArgumentException("d is negative");
        }
        return address = address.add(BigInteger.valueOf(d));
    }

    private void postDecompile() {
        ListIterator<Line> lineListIterator = elementList.getListIterator();
        while (lineListIterator.hasNext()) {
            Line line = lineListIterator.next();
            if (line.getAddressElement() != null) {
                BigInteger address = line.getAddressElement().getAddress();
                String labelName = labelMap.get(address);
                if (labelName != null) {
                    Line labelLine = new Line(null, new LabelElement(labelName));
                    lineListIterator.set(labelLine);
                    lineListIterator.add(line);
                    labelMap.remove(address);
                }
            }
        }
    }
}
