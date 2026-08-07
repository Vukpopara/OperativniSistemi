package Projekat;

import java.util.LinkedList;
import java.util.Queue;

public class ReadyQueue {
    private final Queue<PCB> queue;

    public ReadyQueue() {
        this.queue = new LinkedList<>();
    }

    public synchronized void addProcess(PCB pcb) {
        if (pcb != null) {
            pcb.setState(ProcessState.READY);
            queue.add(pcb);
        }
    }

    public synchronized PCB getNextProcess() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized void clear() {
        queue.clear();
    }
}