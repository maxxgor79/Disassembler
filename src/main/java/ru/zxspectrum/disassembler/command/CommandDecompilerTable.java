package ru.zxspectrum.disassembler.command;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.decompile.DecompilerNamespace;
import ru.zxspectrum.disassembler.render.element.CommandElement;
import ru.zxspectrum.disassembler.settings.Settings;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
@Slf4j
public class CommandDecompilerTable {
    private final Map<String, GroupCommandDecompiler> commandDecompilerMap = new HashMap<>();

    private Settings settings;

    private DecompilerNamespace decompilerNamespace;

    @Getter
    private int maxCommandSize;

    public CommandDecompilerTable(@NonNull Settings settings, @NonNull DecompilerNamespace decompilerNamespace) {
        this.settings = settings;
        this.decompilerNamespace = decompilerNamespace;
    }

    public boolean put(@NonNull PatternPair patternPair) {
        String key = patternPair.getKey();
        GroupCommandDecompiler groupCommandDecompiler = commandDecompilerMap.get(key);
        if (groupCommandDecompiler == null) {
            groupCommandDecompiler = new GroupCommandDecompiler();
            commandDecompilerMap.put(key, groupCommandDecompiler);
        }
        ParameterizedCommandDecompiler commandDecompiler = new ParameterizedCommandDecompiler(patternPair
                , settings, decompilerNamespace);
        maxCommandSize = Math.max(maxCommandSize, commandDecompiler.size());
        return groupCommandDecompiler.add(commandDecompiler);
    }

    public void putAll(@NonNull Collection<PatternPair> col) {
        for (PatternPair patternPair : col) {
            put(patternPair);
        }
    }

    public CommandElement generate(@NonNull String byteCode, @NonNull byte []commandData) throws IOException {
        if (commandData.length == 0) {
            throw new IllegalArgumentException("commandData is empty");
        }
        CommandDecompiler commandDecompiler = commandDecompilerMap.get(byteCode);
        if (commandDecompiler == null) {
            return null;
        }
        return commandDecompiler.decompile(commandData);
    }

    public CommandElement generate(@NonNull byte [] data,  byte []commandData) throws IOException {
        if (data.length == 0) {
            return null;
        }
        return generate(toString(data), commandData);
    }

    public static String toString(@NonNull byte []data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public int size() {
        return this.commandDecompilerMap.size();
    }

    public GroupCommandDecompiler get(String key) {
        return this.commandDecompilerMap.get(key);
    }
}
