package ru.zxspectrum.disassembler.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import ru.zxspectrum.disassembler.lang.Type;

import java.util.Collection;
import java.util.Objects;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
@EqualsAndHashCode
public class PatternPair {
    @Getter
    private String codePattern;

    @Getter
    private String commandPattern;

    public PatternPair(@NonNull String codePattern, @NonNull String commandPattern) {
        this.codePattern = codePattern;
        this.commandPattern = commandPattern;
    }

    public String getKey() {
        int index = codePattern.indexOf('$');
        if (index == -1) {
            return codePattern;
        }
        return codePattern.substring(0, index).toUpperCase();
    }
}
