package Projekat;

import java.util.Scanner;

public class Shell {
    private OSKernel kernel;
    private boolean running;

    public Shell(OSKernel kernel) {
        this.kernel = kernel;
        this.running = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== OS SHELL INICIJALIZOVAN ===");
        System.out.println("Unesite 'help' za prikaz dostupnih komandi.\n");

        while (running) {
            System.out.print("OS_CLI> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();

            switch (command) {
                case "ps":
                    listProcesses();
                    break;
                case "mem":
                    showMemory();
                    break;
                case "run":
                    if (parts.length > 1) {
                        int priority = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;
                        kernel.createProcess(parts[1], priority);
                    } else {
                        System.out.println("Upotreba: run <naziv_programa> [prioritet]");
                    }
                    break;
                case "exit":
                    running = false;
                    System.out.println("Zatvaranje Shell-a...");
                    break;
                case "help":
                    System.out.println("Dostupne komande: ps, mem, run <naziv>, exit");
                    break;
                default:
                    System.out.println("Nepoznata komanda: " + command);
                    break;
            }
        }
    }

    private void listProcesses() {
        System.out.println("\n--- LISTA PROCESA (ps) ---");
        for (PCB p : kernel.getProcessTable()) {
            System.out.println("PID: " + p.getPid() + " | Stanje: " + p.getState() + " | Prioritet: " + p.getPriority());
        }
        System.out.println();
    }

    private void showMemory() {
        System.out.println("\n--- ZAUZEĆE MEMORIJE (mem) ---");
        System.out.println("MemoryManager je aktivan.");
        System.out.println();
    }
}
