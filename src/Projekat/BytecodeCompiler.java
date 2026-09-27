package Projekat;

import java.util.ArrayList;
import java.util.List;

public class BytecodeCompiler {

    public static final byte OP_NOP = 0x00;
    public static final byte OP_MOV = 0x01;
    public static final byte OP_ADD = 0x02;
    public static final byte OP_SUB = 0x03;
    public static final byte OP_HALT = (byte) 0xFF;

    public static final byte REG_A = (byte) 0xF0;
    public static final byte REG_B = (byte) 0xF1;
    public static final byte REG_C = (byte) 0xF2;
    public static final byte REG_D = (byte) 0xF3;

    public static byte[] compile(String asmCode) {
        List<Byte> bytecodeList = new ArrayList<>();
        String[] lines = asmCode.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split("\\s+", 2);
            String opcode = parts[0].toUpperCase();

            switch (opcode) {
                case "NOP":
                    bytecodeList.add(OP_NOP);
                    break;

                case "MOV":
                case "ADD":
                case "SUB":
                    if (opcode.equals("MOV")) bytecodeList.add(OP_MOV);
                    else if (opcode.equals("ADD")) bytecodeList.add(OP_ADD);
                    else if (opcode.equals("SUB")) bytecodeList.add(OP_SUB);

                    if (parts.length > 1) {
                        String[] operands = parts[1].split(",");
                        if (operands.length >= 2) {
                            byte reg = parseRegister(operands[0].trim());
                            byte val = parseValueOrRegister(operands[1].trim());
                            bytecodeList.add(reg);
                            bytecodeList.add(val);
                        }
                    }
                    break;

                case "HALT":
                    bytecodeList.add(OP_HALT);
                    break;

                default:
                    break;
            }
        }

        if (bytecodeList.isEmpty() || bytecodeList.get(bytecodeList.size() - 1) != OP_HALT) {
            bytecodeList.add(OP_HALT);
        }

        byte[] result = new byte[bytecodeList.size()];
        for (int i = 0; i < bytecodeList.size(); i++) {
            result[i] = bytecodeList.get(i);
        }
        return result;
    }

    private static byte parseRegister(String regStr) {
        switch (regStr.toUpperCase()) {
            case "A": return REG_A;
            case "B": return REG_B;
            case "C": return REG_C;
            case "D": return REG_D;
            default: return 0x00;
        }
    }

    private static byte parseValueOrRegister(String valStr) {
        if (valStr.equalsIgnoreCase("A")) return REG_A;
        if (valStr.equalsIgnoreCase("B")) return REG_B;
        if (valStr.equalsIgnoreCase("C")) return REG_C;
        if (valStr.equalsIgnoreCase("D")) return REG_D;
        try {
            return (byte) Integer.parseInt(valStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}