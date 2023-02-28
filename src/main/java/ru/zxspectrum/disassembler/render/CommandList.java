package ru.zxspectrum.disassembler.render;

import java.util.LinkedList;
import java.util.List;

public class CommandList implements Render {
    private final List<Line> lines = new LinkedList<>();

    public void add(Line line) {
        if (line == null) {
            throw new NullPointerException("line");
        }
        lines.add(line);
    }

    public void clear() {
        lines.clear();
    }

    public int size() {
        return lines.size();
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        for (Line line : lines) {
            sb.append(line.generate()).append(System.lineSeparator());
        }
        return sb.toString();
    }
}
