package Projekat;

public class Scheduler {
    private final ReadyQueue readyQueue;
    private final BlockedQueue blockedQueue;
    private final CPU cpu;

    public Scheduler(ReadyQueue readyQueue, BlockedQueue blockedQueue, CPU cpu) {
        this.readyQueue = readyQueue;
        this.blockedQueue = blockedQueue;
        this.cpu = cpu;
    }

    public synchronized void dispatch() {
        PCB current = cpu.getCurrentProcess();

        if (current != null && current.getState() == ProcessState.RUNNING) {
            cpu.saveContext();
            readyQueue.addProcess(current);
        }

        PCB next = readyQueue.getNextProcess();
        if (next != null) {
            next.setState(ProcessState.RUNNING);
            cpu.loadContext(next);
        } else {
            cpu.setCurrentProcess(null);
        }
    }

    public ReadyQueue getReadyQueue() {
        return readyQueue;
    }

    public BlockedQueue getBlockedQueue() {
        return blockedQueue;
    }

    public CPU getCpu() {
        return cpu;
    }
}