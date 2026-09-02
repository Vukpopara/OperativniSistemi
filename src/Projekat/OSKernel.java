package Projekat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OSKernel {
    private List<PCB> processTable;
    private MemoryManager memoryManager;
    private FileSystem fileSystem;
    private int nextPid;
    private Map<String, Integer> registers;
    private int timeQuantum;

    public OSKernel(int ramSize, int timeQuantum) {
        this.processTable = new ArrayList<>();
        this.memoryManager = new MemoryManager(ramSize);
        this.fileSystem = new FileSystem();
        this.timeQuantum = timeQuantum;
        this.nextPid = 1;
        this.registers = new HashMap<>();

        registers.put("A", 0);
        registers.put("B", 0);
        registers.put("C", 0);
        registers.put("D", 0);
    }

    public void boot() {
        System.out.println("[Kernel]: Operativni sistem se pokrece... (RAM: " + memoryManager.getTotalSize() + "B, Quantum: " + timeQuantum + ")");
        Shell shell = new Shell(this, fileSystem);
        shell.start();
    }

    public PCB createProcess(String name, int priority) {
        int processSize = 64;
        MemorySegment segment = memoryManager.allocate(nextPid, processSize);

        int baseAddress = (segment != null) ? segment.getBaseAddress() : 0;
        int limit = processSize;

        PCB pcb = new PCB(nextPid++, priority, baseAddress, limit);
        processTable.add(pcb);
        System.out.println("[Kernel]: Kreiran proces '" + name + "' sa PID: " + pcb.getPid());
        return pcb;
    }

    public void startDMATransfer(int diskBlock, int ramAddress, int size) {
        System.out.println("[DMA Kontroler]: Zapocet direktan prenos sa diska (Blok: " + diskBlock + ") na RAM adresu " + ramAddress + " (Velicina: " + size + "B)...");

        handleInterrupt("DMA_TRANSFER_COMPLETE");
    }

    public void handleInterrupt(String interruptType) {
        System.out.println("[Kernel Interrupt Handler]: Primljen prekid -> " + interruptType);
        if ("DMA_TRANSFER_COMPLETE".equals(interruptType)) {
            System.out.println("[Kernel]: DMA prenos uspjesno zavrsen. Podaci su u RAM-u bez opterecenja CPU-a.");
        }
    }

    public void executeAssemblyInstruction(String instruction) {
        if (instruction == null || instruction.isEmpty()) return;

        String[] parts = instruction.trim().split("\\s+", 2);
        String opcode = parts[0].toUpperCase();

        if (parts.length < 2 && !opcode.equals("NOP")) {
            System.out.println("[Assembler Error]: Neispravna instrukcija -> " + instruction);
            return;
        }

        if (opcode.equals("NOP")) {
            System.out.println("[CPU]: NOP (No Operation)");
            return;
        }

        String[] operands = parts[1].split(",");
        if (operands.length < 2) {
            System.out.println("[Assembler Error]: Instrukcija zahtijeva 2 operanda -> " + instruction);
            return;
        }

        String reg1 = operands[0].trim().toUpperCase();
        String reg2OrVal = operands[1].trim().toUpperCase();

        int val2 = 0;
        if (registers.containsKey(reg2OrVal)) {
            val2 = registers.get(reg2OrVal);
        } else {
            try {
                val2 = Integer.parseInt(reg2OrVal);
            } catch (NumberFormatException e) {
                System.out.println("[Assembler Error]: Nepoznat operand -> " + reg2OrVal);
                return;
            }
        }

        switch (opcode) {
            case "MOV":
                registers.put(reg1, val2);
                System.out.println("[CPU]: MOV " + reg1 + " = " + val2);
                break;
            case "ADD":
                int currentAdd = registers.getOrDefault(reg1, 0);
                registers.put(reg1, currentAdd + val2);
                System.out.println("[CPU]: ADD " + reg1 + " = " + (currentAdd + val2));
                break;
            case "SUB":
                int currentSub = registers.getOrDefault(reg1, 0);
                registers.put(reg1, currentSub - val2);
                System.out.println("[CPU]: SUB " + reg1 + " = " + (currentSub - val2));
                break;
            default:
                System.out.println("[CPU Error]: Nepoznat opcode -> " + opcode);
                break;
        }
    }

    public List<PCB> getProcessTable() { return processTable; }
    public MemoryManager getMemoryManager() { return memoryManager; }
    public FileSystem getFileSystem() { return fileSystem; }
    public Map<String, Integer> getRegisters() { return registers; }
}