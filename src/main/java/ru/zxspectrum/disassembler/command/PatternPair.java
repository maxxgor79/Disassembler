package ru.zxspectrum.disassembler.command;

import ru.zxspectrum.disassembler.lang.Type;

import java.util.Collection;
import java.util.Objects;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class PatternPair {
    private String codePattern;

    private String commandPattern;

    public PatternPair(String codePattern, String commandPattern) {
        if (codePattern == null) {
            throw new NullPointerException("codePattern");
        }
        this.codePattern = codePattern;
        if (commandPattern == null) {
            throw new NullPointerException("commandPattern");
        }
        this.commandPattern = commandPattern;
    }

    public String getCodePattern() {
        return codePattern;
    }

    public String getCommandPattern() {
        return commandPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternPair that = (PatternPair) o;
        return Objects.equals(codePattern, that.codePattern) && Objects.equals(commandPattern, that.commandPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codePattern, commandPattern);
    }

    public String getKey() {
        int index = codePattern.indexOf('$');
        if (index == -1) {
            return codePattern;
        }
        return codePattern.substring(0, index).toUpperCase();
     }
}
