package ru.zxspectrum.disassembler.util;

import java.io.File;

/**
 * @Author: Maxim Gorin
 * Date: 01.03.2023
 */
public final class FileUtil {
    private FileUtil() {

    }

    public static final File createNewFileSameName(File dir, File source, String ext) {
        if (dir == null) {
            throw new NullPointerException("dir");
        }
        if (source == null) {
            throw new NullPointerException("source");
        }
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
