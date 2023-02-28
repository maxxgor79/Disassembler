package ru.zxspectrum.disassembler.command;

import ru.zxspectrum.disassembler.render.element.CommandElement;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
public class CommandDecompilerTable {
    private final Map<String, GroupCommandDecompiler> commandDecompilerMap = new HashMap<>();

    private int maxCommandSize;

    public CommandDecompilerTable() {

    }

    public boolean put(PatternPair patternPair) {
        if (patternPair == null) {
            throw new NullPointerException("patternPair");
        }
        String key = patternPair.getKey();
        GroupCommandDecompiler groupCommandDecompiler = commandDecompilerMap.get(key);
        if (groupCommandDecompiler == null) {
            groupCommandDecompiler = new GroupCommandDecompiler();
            commandDecompilerMap.put(key, groupCommandDecompiler);
        }
        ParameterizedCommandDecompiler commandDecompiler = new ParameterizedCommandDecompiler(patternPair);
        maxCommandSize = Math.max(maxCommandSize, commandDecompiler.size());
        return groupCommandDecompiler.add(commandDecompiler);
    }

    public void putAll(Collection<PatternPair> col) {
        if (col == null) {
            throw new NullPointerException("col");
        }
        for (PatternPair patternPair : col) {
            put(patternPair);
        }
    }

    public CommandElement generate(String byteCode, byte []commandData) throws IOException {
        if (byteCode == null) {
            throw new NullPointerException("col");
        }
        if (commandData == null) {
            throw new NullPointerException("dis");
        }
        if (commandData.length == 0) {
            throw new IllegalArgumentException("commandData is empty");
        }
        CommandDecompiler commandDecompiler = commandDecompilerMap.get(byteCode);
        if (commandDecompiler == null) {
            return null;
        }
        return commandDecompiler.decompile(commandData);
    }

    public CommandElement generate(byte [] data, byte []commandData) throws IOException {
        if (data == null) {
            throw new NullPointerException("data");
        }
        if (data.length == 0) {
            return null;
        }
        return generate(toString(data), commandData);
    }

    public static String toString(byte []data) {
        if (data == null) {
            throw new NullPointerException("data");
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public int getMaxCommandSize() {
        return maxCommandSize;
    }

    public int size() {
        return this.commandDecompilerMap.size();
    }

    public GroupCommandDecompiler get(String key) {
        return this.commandDecompilerMap.get(key);
    }
}
