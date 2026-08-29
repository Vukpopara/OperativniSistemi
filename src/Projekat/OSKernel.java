package Projekat;

import java.util.ArrayList;
import java.util.List;

public class OSKernel {
    private List<PCB> processTable;
    private ReadyQueue readyQueue;
    private BlockedQueue blockedQueue;
    private CPU cpu;
    private Scheduler scheduler;
    private MemoryManager memoryManager;
    private FileSystem fileSystem;
    private IOManager ioManager;
    private int nextPid;

    public OSKernel(int ramSize, int timeQuantum) {
        this.processTable = new ArrayList<>();
        this.readyQueue = new ReadyQueue();
        this.blockedQueue = new BlockedQueue();
        this.cpu = new CPU();
        this.scheduler = new XScheduler(timeQuantum);
        this.memoryManager = new MemoryManager(ramSize);
        this.fileSystem = new FileSystem();
        this.ioManager = new IOManager();
        this.nextPid = 1;

        ioManager.addDevice("Console", new ConsoleDevice());
        if (fileSystem.getDisk() != null) {
            ioManager.addDevice("Disk", fileSystem.getDisk());
        }
    }

    public void boot() {
        System.out.println("==================================================");
        System.out.println("       POKRETANJE OPERATIVNOG SISTEMA (BOOT)      ");
        System.out.println("==================================================\n");

        System.out.println("--- [1] KREIRANJE PROCESA ---");
        createProcess("InitProcess", 1);
        createProcess("UserApp1", 2);
        System.out.println();

        System.out.println("--- [2] TIMERTICK I RASPOREĐIVANJE ---");
        for (int i = 1; i <= 3; i++) {
            System.out.println(">> Takt " + i + " <<");
            timerTick();
        }
        System.out.println();

        System.out.println("--- [3] SYSTEM CALL REQUESTS ---");
        Syscall sysCreate = new Syscall(SyscallType.CREATE_PROCESS, List.of("BackgroundProcess", "3"));
        syscallRequest(sysCreate);

        Syscall sysOpen = new Syscall(SyscallType.OPEN, List.of("/test.txt"));
        syscallRequest(sysOpen);
        System.out.println();

        System.out.println("--- [4] I/O UPRAVLJANJE ---");
        IOOperation ioOp = new IOOperation(IOType.WRITE, "Ispis iz kernela", 1);

        ioManager.submitOperation("Console", ioOp);
        System.out.println();

        System.out.println("--- [5] TERMINACIJA PROCESA ---");
        terminateProcess(1);

        System.out.println("\n==================================================");
        System.out.println("       SIMULACIJA OS-A USPJESNO ZAVRSENA!        ");
        System.out.println("==================================================");
    }

    public int createProcess(String programName, int priority) {
        int defaultMemorySize = 64;
        PCB pcb = new PCB(nextPid++, priority, 0, defaultMemorySize);

        MemorySegment segment = memoryManager.allocate(pcb.getPid(), defaultMemorySize);
        if (segment != null) {
            processTable.add(pcb);
            readyQueue.addProcess(pcb);
            System.out.println("[KERNEL]: Proces '" + programName + "' kreiran sa PID " + pcb.getPid());
            return pcb.getPid();
        } else {
            System.err.println("[KERNEL ERROR]: Nema dovoljno memorije za proces '" + programName + "'");
            return -1;
        }
    }

    public void terminateProcess(int pid) {
        PCB target = null;
        for (PCB p : processTable) {
            if (p.getPid() == pid) {
                target = p;
                break;
            }
        }

        if (target != null) {
            target.setState(ProcessState.TERMINATED);
            memoryManager.deallocate(target.getPid());
            processTable.remove(target);
            if (cpu.getCurrentProcess() != null && cpu.getCurrentProcess().getPid() == pid) {
                cpu.setCurrentProcess(null);
            }
            System.out.println("[KERNEL]: Proces PID " + pid + " je terminisan.");
        }
    }

    public void timerTick() {
        if (cpu.getCurrentProcess() != null) {
            cpu.setProgramCounter(cpu.getProgramCounter() + 1);
        }

        ioManager.tick();

        PCB next = scheduler.chooseNext(readyQueue);
        if (next != null && next != cpu.getCurrentProcess()) {
            cpu.saveContext();
            cpu.loadContext(next);
            next.setState(ProcessState.RUNNING);
        }
    }

    public void syscallRequest(Syscall request) {
        if (request == null) return;

        System.out.println("[KERNEL SYSCALL]: " + request.getType());

        switch (request.getType()) {
            case CREATE_PROCESS:
                String progName = request.getArgs().size() > 0 ? request.getArgs().get(0) : "DefaultProg";
                int priority = request.getArgs().size() > 1 ? Integer.parseInt(request.getArgs().get(1)) : 1;
                createProcess(progName, priority);
                break;

            case EXIT:
                if (cpu.getCurrentProcess() != null) {
                    terminateProcess(cpu.getCurrentProcess().getPid());
                }
                break;

            case READ:
            case WRITE:
            case OPEN:
                System.out.println("[KERNEL]: Obrada Fajl/IO poziva " + request.getType());
                break;

            case SLEEP:
            case YIELD:
                if (cpu.getCurrentProcess() != null) {
                    readyQueue.addProcess(cpu.getCurrentProcess());
                    cpu.setCurrentProcess(null);
                }
                break;

            default:
                break;
        }
    }

    public List<PCB> getProcessTable() { return processTable; }
    public ReadyQueue getReadyQueue() { return readyQueue; }
    public BlockedQueue getBlockedQueue() { return blockedQueue; }
    public CPU getCpu() { return cpu; }
    public Scheduler getScheduler() { return scheduler; }
    public MemoryManager getMemoryManager() { return memoryManager; }
    public FileSystem getFileSystem() { return fileSystem; }
    public IOManager getIOManager() { return ioManager; }
}
