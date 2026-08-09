package Projekat;

public abstract class IODevice {
    protected String name;

    public IODevice(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void startOperation(IOOperation op, PCB p);
    public abstract boolean isBusy();
}