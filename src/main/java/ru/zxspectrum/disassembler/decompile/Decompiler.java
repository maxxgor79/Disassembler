package ru.zxspectrum.disassembler.decompile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.disassembler.bytecode.ParamResult;
import ru.zxspectrum.disassembler.command.CommandDecompilerTable;
import ru.zxspectrum.disassembler.command.PatternPair;
import ru.zxspectrum.disassembler.error.DecompilerException;
import ru.zxspectrum.disassembler.i18n.Messages;
import ru.zxspectrum.disassembler.io.Output;
import ru.zxspectrum.disassembler.io.PatternLoader;
import ru.zxspectrum.disassembler.io.PushbackInputStream;
import ru.zxspectrum.disassembler.render.ElementList;
import ru.zxspectrum.disassembler.render.Line;
import ru.zxspectrum.disassembler.render.element.AddressElement;
import ru.zxspectrum.disassembler.render.element.CommandElement;
import ru.zxspectrum.disassembler.render.element.DbElement;
import ru.zxspectrum.disassembler.render.element.Element;
import ru.zxspectrum.disassembler.render.element.LabelElement;
import ru.zxspectrum.disassembler.render.element.OrgElement;
import ru.zxspectrum.disassembler.render.element.TabElement;
import ru.zxspectrum.disassembler.settings.Settings;
import ru.zxspectrum.disassembler.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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

    private static final String EXT = "asm";

    private Settings settings;
    private CommandDecompilerTable commandDecompilerTable;

    private BigInteger address = BigInteger.ZERO;

    private Map<BigInteger, LabelInfo> labelMap = new HashMap<>();

    private Map<BigInteger, CommandDecompiler> requestedLabelMap = new HashMap<>();

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
            for (String template : settings.getTemplates()) {
                commandDecompilerTable.putAll(loadCommandDecompilerTable(Decompiler.class
                        .getResourceAsStream(template)));
            }
        }
    }

    public void decompile(File file) throws IOException {
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
                CommandElement commandElement = null;
                for (; i < readBytes; i++) {
                    baos.write(commandData[i]);
                    commandElement = commandDecompilerTable.generate(baos.toByteArray(), commandData);
                    if (commandElement != null) {
                        byte[] data = Arrays.copyOfRange(commandData, commandElement.getByteCodeSize()
                                , readBytes);
                        pis.pushback(data);
                        addLine(getAddress(), commandElement);
                        incrementAddress(commandElement.getByteCodeSize());
                        break;
                    }
                }
                if (commandElement == null) {
                    Output.println(Output.warningFormat(Messages.getMessage(Messages.UNKNOWN_COMMAND_CODE), commandData[0]));
                    byte[] data = Arrays.copyOfRange(commandData, 1
                            , readBytes);
                    pis.pushback(data);
                    addLine(getAddress(), new DbElement(commandData[0]));
                    incrementAddress(1);
                }
            }
            postDecompile();
            Output.println(Messages.getMessage(Messages.SUCCESSFULLY_DISASSEMBLED), file.getAbsolutePath());
            File destFile = save(file, elementList.generate());
            Output.println(Messages.getMessage(Messages.FILE_SAVED_IN), destFile.getAbsolutePath());
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

    private File save(File source, String data) throws IOException {
        String fileName = source.getName();
        File outputDir = settings.getOutputDirectory();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File destFile = FileUtil.createNewFileSameName(outputDir, source, EXT);
        OutputStreamWriter os = null;
        try {
            os = new OutputStreamWriter(new FileOutputStream(destFile), settings.getDestEncoding());
            os.write(data);
            return destFile;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                    logger.debug(e);
                }
            }
        }
    }

    private void resetSettings() {
        labelMap.clear();
        elementList.clear();
        requestedLabelMap.clear();
        labelIndex = 1;
        address = settings.getDefaultAddress();
    }

    private void addFirstLine() {
        OrgElement orgElement = new OrgElement(getAddress(), settings.getAddressDimension());
        elementList.add(new Line(null, orgElement));
    }

    private void addLine(BigInteger address, Element commandElement) {
        AddressElement addressElement = new AddressElement(address, settings.getAddressDimension());
        addressElement.setVisible(settings.isAddressVisible());
        Line line = new Line(addressElement, TabElement.TAB, TabElement.TAB, commandElement);
        elementList.add(line);
    }


    @Override
    public BigInteger getAddress() {
        return this.address;
    }

    @Override
    public String addLabelAddress(BigInteger address, boolean required) {
        if (address == null) {
            throw new NullPointerException("address");
        }
        LabelInfo labelInfo = labelMap.get(address);
        if (labelInfo != null) {
            return labelInfo.getName();
        }
        String generatedName = generateLabelName();
        labelMap.put(address, new LabelInfo(generatedName, required));
        return generatedName;
    }

    @Override
    public void addRequestedLabel(BigInteger address, String mask, Collection<ParamResult> params) {
        if (address == null) {
            throw new NullPointerException("address");
        }
        if (mask == null) {
            throw new NullPointerException("mask");
        }
        if (params == null) {
            throw new NullPointerException("params");
        }
        CommandDecompiler commandDecompiler = new CommandDecompiler(this, address, mask, params);
        requestedLabelMap.put(address, commandDecompiler);
    }

    @Override
    public String getLabel(BigInteger address) {
        if (address == null) {
            return null;
        }
        LabelInfo labelInfo = labelMap.get(address);
        if (labelInfo == null) {
            return null;
        }
        return labelInfo.getName();
    }

    private String generateLabelName() {
        return String.format("label_%0" + settings.getAddressDimension() + "d", labelIndex++);
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
        processLabels();
        decompileCommands();
    }

    private void processLabels() {
        Map<BigInteger, LabelInfo> existedLabelMap = new HashMap<>();
        ListIterator<Line> lineListIterator = elementList.getListIterator();
        while (lineListIterator.hasNext()) {
            Line line = lineListIterator.next();
            if (line.getAddressElement() != null) {
                processLabel(existedLabelMap, lineListIterator, line);
            }
        }
        labelMap.clear();
        labelMap.putAll(existedLabelMap);//leave only existed labels
    }

    private void processLabel(Map<BigInteger, LabelInfo> existedLabelMap, ListIterator<Line> lineListIterator, Line line) {
        LabelInfo labelInfo = labelMap.get(line.getAddressElement().getAddress());
        if (labelInfo != null) {
            existedLabelMap.put(line.getAddressElement().getAddress(), labelInfo);
            Line labelLine = new Line(null, new LabelElement(labelInfo.getName()));
            lineListIterator.set(labelLine);
            lineListIterator.add(line);
        }
    }

    private void decompileCommands() {
        ListIterator<Line> lineListIterator = elementList.getListIterator();
        while (lineListIterator.hasNext()) {
            Line line = lineListIterator.next();
            if (line.getAddressElement() != null) {
                updateCommand(line);
            }
        }
    }

    private void updateCommand(Line line) {
        CommandDecompiler commandDecompiler = this.requestedLabelMap.get(line.getAddressElement().getAddress());
        if (commandDecompiler != null) {
            String result = commandDecompiler.render();
            line.replace(CommandElement.class, new CommandElement(result, 1));
        }
    }

    static class LabelInfo {
        private String name;

        private boolean required;

        LabelInfo(String name, boolean required) {
            this.name = name;
            this.required = required;
        }

        String getName() {
            return name;
        }

        boolean isRequired() {
            return required;
        }
    }
}
