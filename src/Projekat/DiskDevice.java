package Projekat;

import java.util.HashMap;
import java.util.Map;

public class DiskDevice extends IODevice {
    private int totalBlocks;
    private int freeBlocks;
    private boolean busy;
    private Map<String, Integer> fileBlockMap;

    public DiskDevice(String name) {
        super(name);
        this.totalBlocks = 1024;
        this.freeBlocks = 1024;
        this.busy = false;
        this.fileBlockMap = new HashMap<>();
    }

    public DiskDevice(String name, int totalBlocks) {
        super(name);
        this.totalBlocks = totalBlocks;
        this.freeBlocks = totalBlocks;
        this.busy = false;
        this.fileBlockMap = new HashMap<>();
    }

    public void allocateFileSpace(File file) {
        if (file == null) return;


        int blocksToAllocate = 1;

        if (freeBlocks >= blocksToAllocate) {
            freeBlocks -= blocksToAllocate;
            fileBlockMap.put(file.getName(), blocksToAllocate);
            System.out.println("[DISK " + name + "]: Alocirano " + blocksToAllocate + " blok(a) za fajl: " + file.getName());
        } else {
            System.out.println("[DISK " + name + " ERROR]: Nema dovoljno slobodnog prostora za: " + file.getName());
        }
    }

    public void freeFileSpace(File file) {
        if (file == null) return;

        if (fileBlockMap.containsKey(file.getName())) {
            int allocated = fileBlockMap.remove(file.getName());
            freeBlocks += allocated;
            System.out.println("[DISK " + name + "]: Oslobođeno " + allocated + " blok(a) za fajl: " + file.getName());
        }
    }