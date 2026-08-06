package Projekat;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private final RAM ram;
    private final List<MemorySegment> segments;

    public MemoryManager(int ramSize) {
        this.ram = new RAM(ramSize);
        this.segments = new ArrayList<>();
        this.segments.add(new MemorySegment(0, ramSize));
    }

    public RAM getRam() {
        return ram;
    }

    public List<MemorySegment> getSegments() {
        return segments;
    }

    public synchronized MemorySegment allocate(int processId, int requestedSize) {
        for (int i = 0; i < segments.size(); i++) {
            MemorySegment segment = segments.get(i);
            if (segment.isFree() && segment.getSize() >= requestedSize) {
                if (segment.getSize() == requestedSize) {
                    segment.setProcessId(processId);
                    return segment;
                } else {
                    MemorySegment allocatedSegment = new MemorySegment(segment.getStartAddress(), requestedSize);
                    allocatedSegment.setProcessId(processId);

                    segment.setStartAddress(segment.getStartAddress() + requestedSize);
                    segment.setSize(segment.getSize() - requestedSize);

                    segments.add(i, allocatedSegment);
                    return allocatedSegment;
                }
            }
        }
        return null;
    }

    public synchronized boolean deallocate(int processId) {
        boolean freed = false;
        for (MemorySegment segment : segments) {
            if (segment.getProcessId() == processId) {
                segment.setFree(true);
                freed = true;
            }
        }
        if (freed) {
            mergeFreeSegments();
        }
        return freed;
    }

    private void mergeFreeSegments() {
        for (int i = 0; i < segments.size() - 1; i++) {
            MemorySegment current = segments.get(i);
            MemorySegment next = segments.get(i + 1);

            if (current.isFree() && next.isFree()) {
                current.setSize(current.getSize() + next.getSize());
                segments.remove(i + 1);
                i--;
            }
        }
    }
}