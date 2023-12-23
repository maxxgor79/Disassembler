package ru.zxspectrum.disassembler.io;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ru.zxspectrum.disassembler.lang.ByteOrder;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author Maxim Gorin
 */
public class RamInputStream extends InputStream {
    @Getter
    @Setter
    @NonNull
    private ByteOrder order = ByteOrder.LittleEndian;

    private byte[] data;

    @Getter
    private long position;

    private boolean closed;

    public RamInputStream(@NonNull byte[] data) {
        this.data = data;
    }

    public RamInputStream(@NonNull byte[] data, @NonNull ByteOrder order) {
        this.data = data;
        this.order = order;
    }

    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public int read() throws IOException {
        ensureOpen();
        if (position >= data.length) {
            return -1;
        }
        return data[(int) position++];
    }

    @Override
    public int available() {
        if (closed) {
            return 0;
        }
        return (int) (data.length - position);
    }

    public void setPosition(long pos) throws IOException {
        if (pos < 0 || pos >= data.length) {
            throw new IllegalArgumentException();
        }
        ensureOpen();
        this.position = pos;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    public long skip(long n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        ensureOpen();
        long newPosition = position + n;
        if (newPosition >= data.length) {
            long d = newPosition - data.length;
            return (n - d);
        }
        position = (int) newPosition;
        return n;
    }

    public int readUnsignedByte() throws IOException {
        ensureOpen();
        int ch = read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    public byte readByte() throws IOException {
        return (byte) readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        ensureOpen();
        int ch1 = read();
        int ch2 = read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return order == ByteOrder.BigEndian ? (ch1 << 8) + (ch2 << 0)
                : (ch2 << 8) + (ch1 << 0);
    }

    public short readShort() throws IOException {
        return (short) readUnsignedShort();
    }

    public int readInt() throws IOException {
        ensureOpen();
        int ch1 = read();
        int ch2 = read();
        int ch3 = read();
        int ch4 = read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return order == ByteOrder.BigEndian ? ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0)) :
                ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }

    private final byte[] readBuffer = new byte[8];

    public long readLong() throws IOException {
        read(readBuffer, 0, 8);
        return order == ByteOrder.LittleEndian ? (((long) readBuffer[0] << 56) +
                ((long) (readBuffer[1] & 255) << 48) +
                ((long) (readBuffer[2] & 255) << 40) +
                ((long) (readBuffer[3] & 255) << 32) +
                ((long) (readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) << 8) +
                ((readBuffer[7] & 255) << 0)) :
                (((long) readBuffer[7] << 56) +
                        ((long) (readBuffer[6] & 255) << 48) +
                        ((long) (readBuffer[5] & 255) << 40) +
                        ((long) (readBuffer[4] & 255) << 32) +
                        ((long) (readBuffer[3] & 255) << 24) +
                        ((readBuffer[1] & 255) << 16) +
                        ((readBuffer[1] & 255) << 8) +
                        ((readBuffer[0] & 255) << 0));
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        if (len == 0) {
            return 0;
        }
        ensureOpen();
        int availableLen = available();
        if (availableLen < len) {
            len = availableLen;
        }
        for (int i = 0; i < len; i++) {
            b[off++] = data[(int) position++];
        }
        return len;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        ensureOpen();
        final int size = available();
        if (size == 0) {
            return new byte[0];
        }
        final byte[] buf = new byte[size];
        read(buf, 0, buf.length);
        return buf;
    }

    public void move(int d) throws IOException {
        long newPosition = position + d;
        if (newPosition < 0L) {
            throw new IllegalArgumentException("position is negative");
        }
        if (newPosition >= data.length) {
            throw new IllegalArgumentException("position is too long");
        }
        position = newPosition;
    }
}
