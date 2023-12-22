package ru.zxspectrum.disassembler.render;

import lombok.NonNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ElementList implements Render {
    private final LinkedList<Line> lines = new LinkedList<>();


    public void add(@NonNull Line ... lines) {
        this.lines.addAll(Arrays.asList(lines));
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
            sb.append(line.generate());
        }
        return sb.toString();
    }

   public ListIterator<Line> getListIterator() {
        return this.lines.listIterator(0);
   }
}
