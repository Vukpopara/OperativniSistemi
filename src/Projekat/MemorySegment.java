package Projekat;

public class MemorySegment {
    private int baseAddress;
    private int size;
    private boolean allocated;
    private int pid;

    public MemorySegment(int baseAddress, int size) {
        this.baseAddress = baseAddress;
        this.size = size;
        this.allocated = false;
        this.pid = -1;
    }

    public MemorySegment(int baseAddress, int size, boolean allocated, int pid) {
        this.baseAddress = baseAddress;
        this.size = size;
        this.allocated = allocated;
        this.pid = pid;
    }

    public int getBaseAddress() { return baseAddress; }
    public int getStart() { return baseAddress; }
    public void setBaseAddress(int baseAddress) { this.baseAddress = baseAddress; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public boolean isAllocated() { return allocated; }
    public void setAllocated(boolean allocated) { this.allocated = allocated; }

    public int getPid() { return pid; }
    public void setPid(int pid) { this.pid = pid; }
}