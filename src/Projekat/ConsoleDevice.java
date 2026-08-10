package Projekat;

public class ConsoleDevice extends IODevice {
    private boolean busy;

    public ConsoleDevice() {
        super("Console");
        this.busy = false;
    }

    @Override
    public void startOperation(IOOperation op, PCB p) {
        if (op == null) return;

        this.busy = true;

        if (op.getType() == IOType.WRITE) {
            int pid = (p != null) ? p.getPid() : -1;
            System.out.println("[CONSOLE OUT - PID " + pid + "]: " + op.getData());
        }

        // Konzolni ispis se u ovoj simulaciji izvršava trenutno
        this.busy = false;
    }

    @Override
    public boolean isBusy() {
        return busy;
    }
}