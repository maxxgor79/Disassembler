package ru.zxspectrum.disassembler.i18n;

import java.util.ResourceBundle;

public final class Messages {
    private Messages() {

    }

    public static final String ERROR = "error";

    public static final String WARNING = "warning";

    public static final String INVALID_COMMAND_TABLE_FORMAT = "invalidCommandTableFormat";

    public static final String VARIABLE_PATTERNS_ARE_NOT_EQUAL = "variablePatternsAreNotEqual";

    public static final String PROGRAM_WELCOME = "programWelcome";

    public static final String WRITTEN_BY = "writtenBy";

    public static final String SUCCESSFULLY_DISASSEMBLED = "successfullyDisassembled";

    public static final String FILE_SAVED_IN = "fileSavedIn";

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.Messages");

    public static String getMessage(String message) {
        if (message == null) {
            return null;
        }
        return resourceBundle.getString(message);
    }
}
