package ru.zxspectrum.disassembler.io;

import ru.zxspectrum.disassembler.lang.ByteOrder;
import ru.zxspectrum.disassembler.lang.Type;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * @Author: Maxim Gorin
 * Date: 25.02.2023
 */
public class PushbackDataInputStream extends InputStream {
    private PushbackInputStream pis;

    private ByteOrder byteOrder;

    private long readBytes;

    public PushbackDataInputStream(InputStream in) {
        this(in, null);
    }

    public PushbackDataInputStream(InputStream in, ByteOrder byteOrder) {
        if (in == null) {
            throw new NullPointerException("in");
        }
        this.pis = new PushbackInputStream(in);
        if (byteOrder == null) {
            byteOrder = ByteOrder.LittleEndian;
        }
        this.byteOrder = byteOrder;
    }

    @Override
    public int read() throws IOException {
        int ch = pis.read();
        if (ch < 0) {
            return -1;
        }
        readBytes++;
        return ch;
    }

    public final byte readByte() throws IOException {
        int ch = pis.read();
        if (ch < 0) {
            throw new EOFException();
        }
        readBytes++;
        return (byte) (ch);
    }

    public final int readUnsignedByte() throws IOException {
        int ch = pis.read();
        if (ch < 0) {
            throw new EOFException();
        }
        readBytes++;
        return ch & 0xFF;
    }

    public int readShort() throws IOException {
        int ch1 = pis.read();
        int ch2 = pis.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        int value = -1;
        switch (byteOrder) {
            case BigEndian -> value = (short) ((ch1 << 8) + (ch2 << 0));
            case LittleEndian -> value = (short) ((ch2 << 8) + (ch1 << 0));
        }
        readBytes += 2;
        return value;
    }

    public int readUnsignedShort() throws IOException {
        int ch1 = pis.read();
        int ch2 = pis.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        int value = -1;
        switch (byteOrder) {
            case BigEndian -> value = (short) ((ch1 << 8) + (ch2 << 0));
            case LittleEndian -> value = (short) ((ch2 << 8) + (ch1 << 0));
        }
        readBytes += 2;
        return value & 0xFFFF;
    }

    public int readInt() throws IOException {
        int ch1 = pis.read();
        int ch2 = pis.read();
        int ch3 = pis.read();
        int ch4 = pis.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        int value = -1;
        switch (byteOrder) {
            case BigEndian -> value = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
            case LittleEndian -> value = ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
        }
        readBytes += 4;
        return value;
    }

    public long readLong() throws IOException {
        int ch1 = pis.read();
        int ch2 = pis.read();
        int ch3 = pis.read();
        int ch4 = pis.read();
        int ch5 = pis.read();
        int ch6 = pis.read();
        int ch7 = pis.read();
        int ch8 = pis.read();
        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0)
            throw new EOFException();
        long value = -1;
        switch (byteOrder) {
            case BigEndian -> value = ((long) ch1 << 56) + ((long) ch2 << 48) + ((long) ch3 << 40) + ((long) ch4 << 32)
                    + (((long) ch5 << 24) + (ch6 << 16) + (ch7 << 8) + (ch8 << 0));
            case LittleEndian ->
                    value = ((long) ch7 << 56) + ((long) ch7 << 48) + ((long) ch6 << 40) + ((long) ch5 << 32)
                            + (((long) ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
        }
        readBytes += 8;
        return value;
    }

    public BigInteger read(Type type) throws IOException {
        if (type == null) {
            throw new NullPointerException("type");
        }
        BigInteger result = null;
        switch (type) {
            case Int8 -> {result = BigInteger.valueOf(readByte());}
            case UInt8 -> {result = BigInteger.valueOf(readUnsignedByte());}
            case Int16 -> {result = BigInteger.valueOf(readShort());}
            case UInt16 -> {result = BigInteger.valueOf(readUnsignedShort());}
            case Int32, UInt32 -> {result = BigInteger.valueOf(readInt());}
            case Int64, UInt64 -> {result = BigInteger.valueOf(readLong());}
        }
        return result;
    }

    public long getReadBytes() {
        return readBytes;
    }

    public void unread(int b) throws IOException {
        pis.pushback(b);
        readBytes--;
    }

    public void unread(byte[] data) throws IOException {
        System.out.println("read:"+readBytes);
        pis.pushback(data);
        readBytes -= data.length;
    }

    public void unread(byte[] data, int off, int len) throws IOException {
        pis.pushback(data, off, len);
        readBytes -= (len - off);
    }
}
