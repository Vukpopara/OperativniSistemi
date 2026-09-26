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
    private int timeQuantum;
    private Map<String, String> virtualFiles;

    public OSKernel(int ramSize, int timeQuantum) {
        this.processTable = new ArrayList<>();
        this.memoryManager = new MemoryManager(ramSize);
        this.fileSystem = new FileSystem();
        this.timeQuantum = timeQuantum;
        this.nextPid = 1;
        this.virtualFiles = new HashMap<>();
    }

    public void boot() {
        System.out.println("=================================================");
        System.out.println("     POKRETANJE OPERATIVNOG SISTEMA (BOOT)...    ");
        System.out.println("=================================================");

        initFileSystemDefaults();
        bootSystemProcesses();

        System.out.println("\n[Kernel]: Sistem uspješno podignut!");
        System.out.println("=================================================\n");

        Shell shell = new Shell(this, fileSystem);
        shell.start();
    }

    private void initFileSystemDefaults() {
        System.out.println("[Boot Step 1]: Inicijalizacija fajl sistema...");

        try {
            fileSystem.createDirectory("/bin");
            fileSystem.createDirectory("/system");
            fileSystem.createDirectory("/home");
            fileSystem.createFile("/system/boot.sys");
            fileSystem.createFile("/home/demo.asm");
        } catch (Exception e) {
        }

        String sampleAsm = "MOV A, 10\n" +
                "ADD A, 5\n" +
                "MOV B, 20\n" +
                "SUB B, 4\n" +
                "HALT";

        virtualFiles.put("/home/demo.asm", sampleAsm);

        System.out.println("   -> Kreirani folderi: /bin, /system, /home");
        System.out.println("   -> Kreiran fajl: /home/demo.asm sa asemblerskim kodom.");
    }

    private void bootSystemProcesses() {
        System.out.println("[Boot Step 2]: Pokretanje sistemskih procesa...");
        createProcess("Sys_Init", 0, true);
        createProcess("Mem_Manager_Daemon", 0, true);
    }

    public PCB createProcess(String name, int priority, boolean isSystem) {
        for (PCB p : processTable) {
            if (p.getName().equalsIgnoreCase(name)) {
                System.out.println("[Kernel Greška]: Proces '" + name + "' već postoji!");
                return null;
            }
        }

        int processSize = 64;
        int baseAddress = (nextPid - 1) * processSize;

        PCB pcb = new PCB(nextPid++, name, priority, baseAddress, processSize, isSystem);
        pcb.setState(ProcessState.READY);
        processTable.add(pcb);

        System.out.println("   [Proces Kreiran]: " + pcb.toString());
        return pcb;
    }

    public PCB createProcess(String name, int priority) {
        return createProcess(name, priority, false);
    }

    public void executeAssemblyFile(String filePath) {
        System.out.println("\n--- [IZVRŠAVANJE ASEMBLERSKOG FAJLA: " + filePath + "] ---");

        String asmContent = virtualFiles.get(filePath);

        if (asmContent == null || asmContent.isEmpty()) {
            asmContent = "MOV A, 10\nADD A, 5\nMOV B, 20\nSUB B, 4\nHALT";
        }

        System.out.println("1. Izvorni asemblerski tekst u fajlu:\n" + asmContent);

        byte[] bytecode = BytecodeCompiler.compile(asmContent);
        System.out.print("2. Prevedeni bajtkod (Hex): ");
        for (byte b : bytecode) {
            System.out.printf("0x%02X ", b);
        }
        System.out.println("\n");

        PCB proc = createProcess("AsmProc_" + filePath.replaceAll("[^a-zA-Z0-9]", ""), 1, false);
        if (proc == null) return;

        proc.loadBytecodeToMemory(bytecode);
        proc.setState(ProcessState.RUNNING);

        System.out.println("3. Pokretanje virtuelnog CPU-a nad RAM memorijom procesa...");
        runCpuOnMemory(proc);

        proc.setState(ProcessState.TERMINATED);
        System.out.println("--- [IZVRŠAVANJE ZAVRŠENO] ---\n");
    }

    private void runCpuOnMemory(PCB proc) {
        byte[] ram = proc.getMemoryImage();
        int pc = proc.getProgramCounter();
        boolean running = true;

        while (running && pc < ram.length) {
            byte opcode = ram[pc++];

            switch (opcode) {
                case BytecodeCompiler.OP_NOP:
                    System.out.println("   [CPU PC=0x" + String.format("%02X", pc - 1) + "]: NOP");
                    break;

                case BytecodeCompiler.OP_MOV:
                case BytecodeCompiler.OP_ADD:
                case BytecodeCompiler.OP_SUB:
                    if (pc + 1 >= ram.length) { running = false; break; }
                    byte regCode = ram[pc++];
                    byte valCode = ram[pc++];

                    String regName = getRegisterName(regCode);
                    int currentVal = proc.getRegisters().getOrDefault(regName, 0);
                    int valToUse = isRegister(valCode) ? proc.getRegisters().getOrDefault(getRegisterName(valCode), 0) : valCode;

                    int newVal = currentVal;
                    if (opcode == BytecodeCompiler.OP_MOV) newVal = valToUse;
                    else if (opcode == BytecodeCompiler.OP_ADD) newVal = currentVal + valToUse;
                    else if (opcode == BytecodeCompiler.OP_SUB) newVal = currentVal - valToUse;

                    proc.getRegisters().put(regName, newVal);

                    String opName = (opcode == BytecodeCompiler.OP_MOV) ? "MOV" : (opcode == BytecodeCompiler.OP_ADD) ? "ADD" : "SUB";
                    System.out.println("   [CPU PC=0x" + String.format("%02X", pc - 3) + "]: " + opName + " " + regName + ", " + valToUse + " -> Registar " + regName + " = " + newVal);
                    break;

                case BytecodeCompiler.OP_HALT:
                    System.out.println("   [CPU PC=0x" + String.format("%02X", pc - 1) + "]: HALT (Prekid rada CPU-a)");
                    running = false;
                    break;

                default:
                    running = false;
                    break;
            }
        }
        proc.setProgramCounter(pc);
        System.out.println("   Finalno stanje registara procesa " + proc.getName() + ": " + proc.getRegisters());
    }

    private String getRegisterName(byte regCode) {
        switch (regCode) {
            case BytecodeCompiler.REG_A: return "A";
            case BytecodeCompiler.REG_B: return "B";
            case BytecodeCompiler.REG_C: return "C";
            case BytecodeCompiler.REG_D: return "D";
            default: return "A";
        }
    }

    private boolean isRegister(byte code) {
        return (code & 0xFF) >= 0xF0 && (code & 0xFF) <= 0xF3;
    }

    public List<PCB> getProcessTable() { return processTable; }
    public MemoryManager getMemoryManager() { return memoryManager; }
    public FileSystem getFileSystem() { return fileSystem; }
    public Map<String, String> getVirtualFiles() { return virtualFiles; }
}