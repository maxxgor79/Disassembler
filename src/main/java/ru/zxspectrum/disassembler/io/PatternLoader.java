package ru.zxspectrum.disassembler.io;

import ru.zxspectrum.disassembler.command.PatternPair;
import ru.zxspectrum.disassembler.error.ParserException;
import ru.zxspectrum.disassembler.i18n.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class PatternLoader {
    public Collection<PatternPair> load(InputStream is) throws IOException {
        return load(is, Charset.defaultCharset());
    }

    private static boolean isPatternVariablesAreEqual(String codePattern, String commandPattern) {
        PatternParameterScanner scanner1 = new PatternParameterScanner(codePattern);
        PatternParameterScanner scanner2 = new PatternParameterScanner(commandPattern);
        while(true) {
            boolean hasNextVariable1 = scanner1.hasNextVariable();
            boolean hasNextVariable2 = scanner2.hasNextVariable();
            if (hasNextVariable1 && hasNextVariable2) {
                if (!scanner1.nextVariable().equals(scanner2.nextVariable()))  {
                    return false;
                }
            } else {
                if (!hasNextVariable1 && !hasNextVariable2) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public Collection<PatternPair> load(InputStream is, Charset charset) throws IOException {
        if (is == null) {
            throw new NullPointerException("is");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        Scanner scanner = new Scanner(is, charset);
        scanner.useDelimiter("[\t\r\n]");
        int lineNumber = 1;
        String codePattern = null;
        String commandPattern = null;
        List<PatternPair> resultList = new LinkedList<>();
        try {
            while (scanner.hasNextLine()) {
                codePattern = scanner.next().trim();
                commandPattern = scanner.next().trim();
                if (!isPatternVariablesAreEqual(codePattern, commandPattern)) {
                    throw new ParserException(lineNumber, Messages.getMessage(Messages.VARIABLE_PATTERNS_ARE_NOT_EQUAL)
                            , String.format("%s\t%s"), codePattern, commandPattern);
                }
                parse(resultList, codePattern, commandPattern);
                lineNumber++;
            }
        } catch(NoSuchElementException e) {
            throw new ParserException(lineNumber, Messages.getMessage(Messages.INVALID_COMMAND_TABLE_FORMAT)
                    , String.format("%s\t%s"), codePattern, commandPattern);
        }
        return resultList;
    }

    protected void parse(Collection<PatternPair> pairCollection, String codePattern
            , String commandPattern) {
        pairCollection.add(new PatternPair(codePattern, commandPattern));
    }
}
