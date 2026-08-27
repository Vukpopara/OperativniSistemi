package Projekat;

public class XScheduler implements Scheduler {
    private int timeQuantum;

    public XScheduler(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    @Override
    public PCB chooseNext(ReadyQueue ready) {
        if (ready == null || ready.isEmpty()) {
            return null;
        }
        return ready.getNextProcess();
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }
}