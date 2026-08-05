package Projekat;

public class MemorySegment {
    private int startAddress;
    private int size;
    private boolean isFree;
    private int processId;

    public MemorySegment(int startAddress, int size) {
        this.startAddress = startAddress;
        this.size = size;
        this.isFree = true;
        this.processId = -1;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
        if (free) {
            this.processId = -1;
        }
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
        this.isFree = (processId == -1);
    }

    @Override
    public String toString() {
        return "MemorySegment{" +
                "startAddress=" + startAddress +
                ", size=" + size +
                ", isFree=" + isFree +
                ", processId=" + processId +
                '}';
    }
}