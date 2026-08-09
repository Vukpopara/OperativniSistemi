package Projekat;

public class XScheduler extends Scheduler {

    public XScheduler(ReadyQueue readyQueue, BlockedQueue blockedQueue, CPU cpu) {
        super(readyQueue, blockedQueue, cpu);
    }

    @Override
    public synchronized void dispatch() {
        PCB current = getCpu().getCurrentProcess();

        if (current != null && (current.getState() == ProcessState.TERMINATED || current.getState() == ProcessState.BLOCKED)) {
            getCpu().setCurrentProcess(null);
        }

        super.dispatch();
    }

    public synchronized void unblockProcess() {
        PCB unblocked = getBlockedQueue().unblockNext();
        if (unblocked != null) {
            getReadyQueue().addProcess(unblocked);
        }
    }
}