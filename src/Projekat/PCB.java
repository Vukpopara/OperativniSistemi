package Projekat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCB {
    private int pid;
    private String name;
    private ProcessState state;
    private int priority;
    private int programCounter;
    private Map<String, Integer> registers;
    private int baseAddress;
    private int limit;
    private boolean isSystemProcess;
    private byte[] memoryImage;

    public PCB(int pid, String name, int priority, int baseAddress, int limit, boolean isSystemProcess) {
        this.pid = pid;
        this.name = name;
        this.priority = priority;
        this.state = ProcessState.NEW;
        this.programCounter = 0;
        this.registers = new HashMap<>();
        this.baseAddress = baseAddress;
        this.limit = limit;
        this.isSystemProcess = isSystemProcess;
        this.memoryImage = new byte[limit];

        registers.put("A", 0);
        registers.put("B", 0);
        registers.put("C", 0);
        registers.put("D", 0);
    }

    public int getPid() { return pid; }
    public String getName() { return name; }
    public ProcessState getState() { return state; }
    public void setState(ProcessState state) { this.state = state; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public int getProgramCounter() { return programCounter; }
    public void setProgramCounter(int programCounter) { this.programCounter = programCounter; }
    public Map<String, Integer> getRegisters() { return registers; }
    public int getBaseAddress() { return baseAddress; }
    public void setBaseAddress(int baseAddress) { this.baseAddress = baseAddress; }
    public int getLimit() { return limit; }
    public boolean isSystemProcess() { return isSystemProcess; }
    public byte[] getMemoryImage() { return memoryImage; }

    public void loadBytecodeToMemory(byte[] code) {
        for (int i = 0; i < code.length && i < memoryImage.length; i++) {
            memoryImage[i] = code[i];
        }
    }

    @Override
    public String toString() {
        return String.format("PCB{PID=%d, Name='%s', Type=%s, State=%s, Priority=%d, RAM=[0x%X - 0x%X]}",
                pid, name, (isSystemProcess ? "SYSTEM" : "USER"), state, priority, baseAddress, (baseAddress + limit));
    }
}