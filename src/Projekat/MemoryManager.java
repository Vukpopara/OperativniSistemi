package Projekat;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private int totalSize;
    private List<MemorySegment> segments;

    public MemoryManager(int totalSize) {
        this.totalSize = totalSize;
        this.segments = new ArrayList<>();
        this.segments.add(new MemorySegment(0, totalSize));
    }

    public MemorySegment allocate(int pid, int size) {
        for (int i = 0; i < segments.size(); i++) {
            MemorySegment seg = segments.get(i);

            if (!seg.isAllocated() && seg.getSize() >= size) {
                if (seg.getSize() > size) {
                    MemorySegment allocatedSeg = new MemorySegment(seg.getBaseAddress(), size, true, pid);
                    MemorySegment freeSeg = new MemorySegment(seg.getBaseAddress() + size, seg.getSize() - size, false, -1);

                    segments.set(i, allocatedSeg);
                    segments.add(i + 1, freeSeg);
                    return allocatedSeg;
                } else {
                    seg.setAllocated(true);
                    seg.setPid(pid);
                    return seg;
                }
            }
        }
        return null;
    }

    public void deallocate(int pid) {
        for (MemorySegment seg : segments) {
            if (seg.isAllocated() && seg.getPid() == pid) {
                seg.setAllocated(false);
                seg.setPid(-1);
            }
        }
        mergeFreeSegments();
    }

    private void mergeFreeSegments() {
        for (int i = 0; i < segments.size() - 1; i++) {
            MemorySegment current = segments.get(i);
            MemorySegment next = segments.get(i + 1);

            if (!current.isAllocated() && !next.isAllocated()) {
                current.setSize(current.getSize() + next.getSize());
                segments.remove(i + 1);
                i--;
            }
        }
    }

    public void defragment() {
        System.out.println("[MemoryManager]: Pokrenuta defragmentacija...");
        List<MemorySegment> newSegments = new ArrayList<>();
        int currentAddress = 0;
        int totalFreeSize = 0;

        for (MemorySegment seg : segments) {
            if (seg.isAllocated()) {
                MemorySegment movedSeg = new MemorySegment(currentAddress, seg.getSize(), true, seg.getPid());
                newSegments.add(movedSeg);
                currentAddress += seg.getSize();
            } else {
                totalFreeSize += seg.getSize();
            }
        }

        if (totalFreeSize > 0) {
            MemorySegment freeSeg = new MemorySegment(currentAddress, totalFreeSize, false, -1);
            newSegments.add(freeSeg);
        }

        this.segments = newSegments;
        System.out.println("[MemoryManager]: Defragmentacija uspjesno zavrsena.");
    }

    public int getTotalSize() { return totalSize; }
    public List<MemorySegment> getSegments() { return segments; }
}