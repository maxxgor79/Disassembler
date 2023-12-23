package ru.zxspectrum.disassembler.io;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Maxim Gorin
 * Date: 28.02.2023
 */
public class PushbackInputStream extends InputStream {
    private byte [] buf = new byte[256];

    private int pos;

    private int len;

    private InputStream is;

    public PushbackInputStream(@NonNull InputStream is) {
        this.is = is;
    }
    @Override
    public int read() throws IOException {
        if (pos == len) {
            return is.read();
        }
        return buf[pos++];
    }

    public void pushback(int val) {
        pos = 0;
        len = 1;
        buf[pos] = (byte)val;
    }

    public void pushback(byte []data) {
        pushback(data, 0, data.length);
    }

    public void pushback(@NonNull byte []data, int off, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        if (len == 0) {
            return;
        }
        if (len > buf.length) {
            throw new IllegalArgumentException("buffer is overflow");
        }
        for (int i = 0; i < len; i++) {
            buf[i] = data[off + i];
        }
        this.pos = 0;
        this.len = len;
    }

    @Override
    public int read(@NonNull byte []data, int off, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        if (len == 0) {
            return 0;
        }
        if (this.pos == this.len) {
            return is.read(data, off, len);
        }
        int readBytes = 0;
        while ((len > 0) && (pos < this.len)) {
            data[off++] = buf[pos++];
            readBytes++;
            len--;
        }
        if (len > 0) {
            int actualReadBytes = is.read(data, off, len);
            readBytes += actualReadBytes == -1 ? 0: actualReadBytes;
        }
        return readBytes;
    }

    public int read(byte []data) throws IOException {
        return read(data, 0, data.length);
    }
}
