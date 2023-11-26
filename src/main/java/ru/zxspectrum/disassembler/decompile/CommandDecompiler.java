package ru.zxspectrum.disassembler.decompile;

import lombok.extern.slf4j.Slf4j;
import ru.zxspectrum.disassembler.bytecode.ParamResult;
import ru.zxspectrum.disassembler.util.ConverterUtil;
import ru.zxspectrum.disassembler.util.TypeUtil;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author: Maxim Gorin
 * Date: 02.03.2023
 */
@Slf4j
public class CommandDecompiler {
    private BigInteger address;

    private DecompilerNamespace decompilerNamespace;

    private String mask;

    private Collection<ParamResult> params;


    public CommandDecompiler(DecompilerNamespace decompilerNamespace, BigInteger address
            , String mask, Collection<ParamResult> params) {
        if (decompilerNamespace == null) {
            throw new NullPointerException("decompilerNamespace");
        }
        this.decompilerNamespace = decompilerNamespace;
        if (address == null) {
            throw new NullPointerException("address");
        }
        this.address = address;
        if (mask == null) {
            throw new NullPointerException("mask");
        }
        this.mask = mask;
        if (params == null) {
            throw new NullPointerException("params");
        }
        this.params = params;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(mask);
        scanner.useDelimiter("[$][ando]+");
        Iterator<ParamResult> iterator = params.iterator();
        while(scanner.hasNext()) {
            String s = scanner.next();
            sb.append(s);
            if (iterator.hasNext()) {
                ParamResult param  = iterator.next();
                sb.append(renderParameter(param));
            }
        }
        return sb.toString();
    }

    private String renderParameter(ParamResult param) {
        BigInteger value = param.getValue();
        if (TypeUtil.isAddressOffsetPattern(param.getPatternParam())) {
           value = address.add(value);
           String labelName = decompilerNamespace.getLabel(value);
           if (labelName != null) {
               return labelName;
           }
        } else {
            if (TypeUtil.isAddressPattern(param.getPatternParam())) {
                String labelName = decompilerNamespace.getLabel(value);
                if (labelName != null) {
                    return labelName;
                }
            }
        }
        return ConverterUtil.toTextHexadecimal(value, "$", null);
    }
}
