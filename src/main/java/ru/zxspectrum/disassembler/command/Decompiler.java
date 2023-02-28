package ru.zxspectrum.disassembler.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.zxspectrum.disassembler.error.DecompilerException;
import ru.zxspectrum.disassembler.io.PatternLoader;
import ru.zxspectrum.disassembler.io.PushbackInputStream;
import ru.zxspectrum.disassembler.render.element.CommandElement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
public class Decompiler {
    private static final Logger logger = LogManager.getLogger(Decompiler.class.getName());

    private CommandDecompilerTable commandDecompilerTable;

    public Decompiler() {
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
            commandDecompilerTable = new CommandDecompilerTable();
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

    private static void copy(byte[] commandData, int bufferOffset, int bufferLength) {
        for (int i = 0; i < bufferLength; i++) {
            commandData[i] = commandData[i + bufferOffset];
        }
    }

    public String decompile(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file");

        }
        if (this.commandDecompilerTable.size() == 0) {
            throw new DecompilerException("Command table is empty");
        }
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        BigInteger offset = BigInteger.ZERO;
        try {
            fis = new FileInputStream(file);
            PushbackInputStream pis = new PushbackInputStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] commandData = new byte[commandDecompilerTable.getMaxCommandSize()];
            while (true) {
                final int readBytes = pis.read(commandData);
                if (readBytes == -1) {
                    break;
                }
                baos.reset();
                int i = 0;
                ;
                for (; i < readBytes; i++) {
                    baos.write(commandData[i]);
                    CommandElement commandElement = commandDecompilerTable.generate(baos.toByteArray(), commandData);
                    if (commandElement != null) {
                        byte[] data = Arrays.copyOfRange(commandData, commandElement.getByteCodeSize()
                                , readBytes);
                        pis.pushback(data);
                        sb.append(commandElement.getText()).append(System.lineSeparator());
                        System.out.println(String.format("%04X", offset) + "\t" + commandElement.getText());
                        offset = offset.add(BigInteger.valueOf(commandElement.getByteCodeSize()));
                        break;
                    }
                }
                if (i >= readBytes) {
                    throw new DecompilerException("Command is not found");
                }
            }
            return sb.toString();
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


}
