package Projekat;

public class RAM {
    private final int totalSize;
    private final byte[] memory;

    public RAM(int totalSize) {
        this.totalSize = totalSize;
        this.memory = new byte[totalSize];
    }

    public int getTotalSize() {
        return totalSize;
    }

    public byte read(int address) {
        if (address < 0 || address >= totalSize) {
            throw new IndexOutOfBoundsException("Nevalidna memorijska adresa: " + address);
        }
        return memory[address];
    }

    public void write(int address, byte data) {
        if (address < 0 || address >= totalSize) {
            throw new IndexOutOfBoundsException("Nevalidna memorijska adresa: " + address);
        }
        memory[address] = data;
    }

    public void clear() {
        for (int i = 0; i < totalSize; i++) {
            memory[i] = 0;
        }
    }
}