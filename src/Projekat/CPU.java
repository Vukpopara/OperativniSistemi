package Projekat;

import java.util.HashMap;
import java.util.Map;

public class CPU {
    private int programCounter;
    private Map<String, Integer> registers;
    private PCB currentProcess;

    public CPU() {
        this.programCounter = 0;
        this.registers = new HashMap<>();
        this.currentProcess = null;
    }

    public void loadContext(PCB pcb) {
        this.currentProcess = pcb;
        if (pcb != null) {
            this.programCounter = pcb.getProgramCounter();
            this.registers = new HashMap<>(pcb.getRegisters());
        }
    }

    public void saveContext() {
        if (currentProcess != null) {
            currentProcess.setProgramCounter(programCounter);
            currentProcess.getRegisters().clear();
            currentProcess.getRegisters().putAll(registers);
        }
    }

    public PCB getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(PCB currentProcess) {
        this.currentProcess = currentProcess;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public Map<String, Integer> getRegisters() {
        return registers;
    }

    public void setRegister(String name, int value) {
        this.registers.put(name, value);
    }

    public int getRegister(String name) {
        return this.registers.getOrDefault(name, 0);
    }
}