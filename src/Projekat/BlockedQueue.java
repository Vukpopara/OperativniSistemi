package Projekat;

import java.util.LinkedList;
import java.util.Queue;

public class BlockedQueue {
    private final Queue<PCB> queue;

    public BlockedQueue() {
        this.queue = new LinkedList<>();
    }

    public synchronized void addProcess(PCB pcb) {
        if (pcb != null) {
            pcb.setState(ProcessState.BLOCKED);
            queue.add(pcb);
        }
    }

    public synchronized PCB unblockNext() {
        return queue.poll();
    }

    public synchronized boolean removeProcess(PCB pcb) {
        return queue.remove(pcb);
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }
}