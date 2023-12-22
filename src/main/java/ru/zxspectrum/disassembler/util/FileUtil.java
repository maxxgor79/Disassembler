package ru.zxspectrum.disassembler.util;

import lombok.NonNull;

import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2023
 */
public final class FileUtil {
    private FileUtil() {

    }

    public static final File createNewFileSameName(@NonNull File dir, @NonNull File source, String ext) {
        String fileName = source.getName();
        int index = fileName.indexOf('.');
        if (index != -1) {
            fileName = fileName.substring(0, index);
        }
        if (ext != null && !ext.trim().isEmpty()) {
            fileName = fileName + "." + ext;
        }
        return new File(dir, fileName);
    }
}
