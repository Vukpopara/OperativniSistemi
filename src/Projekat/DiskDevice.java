package Projekat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiskDevice extends IODevice {
    private int totalBlocks;
    private int freeBlocks;
    private boolean busy;
    private Map<String, List<Integer>> linkedBlocksMap;

    public DiskDevice(String name) {
        super(name);
        this.totalBlocks = 1024;
        this.freeBlocks = 1024;
        this.busy = false;
        this.linkedBlocksMap = new HashMap<>();
    }

    public DiskDevice(String name, int totalBlocks) {
        super(name);
        this.totalBlocks = totalBlocks;
        this.freeBlocks = totalBlocks;
        this.busy = false;
        this.linkedBlocksMap = new HashMap<>();
    }

    @Override
    public void startOperation(IOOperation op, PCB p) {
        this.busy = true;
        System.out.println("[DISK " + name + "]: Zapoceta operacija " + (op != null ? op.getType() : "UNKNOWN") + " za PID " + (p != null ? p.getPid() : -1));
        this.busy = false;
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    public void allocateFileSpace(File file) {
        if (file == null) return;

        int blocksToAllocate = 2;

        if (freeBlocks >= blocksToAllocate) {
            freeBlocks -= blocksToAllocate;
            List<Integer> allocatedBlocks = new ArrayList<>();
            int startBlock = (int) (Math.random() * 500);

            allocatedBlocks.add(startBlock);
            allocatedBlocks.add(startBlock + 1);

            linkedBlocksMap.put(file.getName(), allocatedBlocks);
            System.out.println("[DISK " + name + "]: Ulancani blokovi " + allocatedBlocks + " alocirani za fajl: " + file.getName());
        } else {
            System.out.println("[DISK " + name + " ERROR]: Nema dovoljno slobodnog prostora za: " + file.getName());
        }
    }

    public void freeFileSpace(File file) {
        if (file == null) return;

        if (linkedBlocksMap.containsKey(file.getName())) {
            List<Integer> freed = linkedBlocksMap.remove(file.getName());
            freeBlocks += freed.size();
            System.out.println("[DISK " + name + "]: Oslobodjeni ulancani blokovi " + freed + " za fajl: " + file.getName());
        }
    }

    public Map<String, List<Integer>> getLinkedBlocksMap() {
        return linkedBlocksMap;
    }
}