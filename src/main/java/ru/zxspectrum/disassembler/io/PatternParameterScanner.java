package ru.zxspectrum.disassembler.io;

import lombok.NonNull;
import ru.zxspectrum.disassembler.util.TypeUtil;

import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.stream.Stream;

/**
 * @Author: Maxim Gorin
 * Date: 26.02.2023
 */
public class PatternParameterScanner {
    private Iterator<MatchResult> iter;

    public PatternParameterScanner(@NonNull String pattern) {
        Scanner scanner = new Scanner(pattern);
        this.iter = scanner.findAll("[$][aond]+").iterator();
    }

    public boolean hasNextVariable() {
        return iter.hasNext();
    }

    public String nextVariable() {
        return iter.next().group();
    }
}
