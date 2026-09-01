package Projekat;

import java.util.Scanner;

public class Shell {
    private OSKernel kernel;
    private FileSystem fileSystem;
    private Directory currentDir;
    private boolean running;

    public Shell(OSKernel kernel, FileSystem fileSystem) {
        this.kernel = kernel;
        this.fileSystem = fileSystem;
        this.currentDir = fileSystem.getRoot();
        this.running = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== OS SHELL INICIJALIZOVAN ===");
        System.out.println("Unesite 'help' za prikaz komandi.\n");

        while (running) {
            System.out.print("OS_CLI:" + getFullPath(currentDir) + "> ");
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
                        System.out.println("Upotreba: run <naziv>");
                    }
                    break;
                case "mkdir":
                    if (parts.length > 1) {
                        fileSystem.createDirectory(getFullPath(currentDir) + parts[1]);
                        System.out.println("Kreiran direktorijum: " + parts[1]);
                    } else {
                        System.out.println("Upotreba: mkdir <naziv>");
                    }
                    break;
                case "dir":
                case "ls":
                    System.out.println("--- SADRŽAJ DIREKTORIJUMA ---");
                    for (FsNode node : currentDir.getChildrenMap().values()) {
                        String type = (node instanceof Directory) ? "<DIR>" : "<FILE>";
                        System.out.println(type + "\t" + node.getName());
                    }
                    break;
                case "cd":
                    if (parts.length > 1) {
                        changeDirectory(parts[1]);
                    } else {
                        System.out.println("Upotreba: cd <putanja>");
                    }
                    break;
                case "rm":
                    if (parts.length > 1) {
                        fileSystem.delete(getFullPath(currentDir) + parts[1]);
                    } else {
                        System.out.println("Upotreba: rm <naziv>");
                    }
                    break;
                case "exit":
                    running = false;
                    System.out.println("Zatvaranje Shell-a...");
                    break;
                case "help":
                    System.out.println("Komande: ps, mem, run, mkdir, dir, ls, cd, rm, exit");
                    break;
                default:
                    System.out.println("Nepoznata komanda: " + command);
                    break;
            }
        }
    }

    private void changeDirectory(String path) {
        if (path.equals("..")) {
            if (currentDir.getParent() != null) {
                currentDir = currentDir.getParent();
            }
        } else {
            FsNode next = currentDir.getChild(path);
            if (next instanceof Directory) {
                currentDir = (Directory) next;
            } else {
                System.out.println("Direktorijum ne postoji: " + path);
            }
        }
    }

    private String getFullPath(Directory dir) {
        if (dir.getParent() == null) return "/";
        return getFullPath(dir.getParent()) + dir.getName() + "/";
    }

    private void listProcesses() {
        System.out.println("\n--- LISTA PROCESA (ps) ---");
        for (PCB p : kernel.getProcessTable()) {
            System.out.println("PID: " + p.getPid() + " | Stanje: " + p.getState());
        }
    }

    private void showMemory() {
        System.out.println("\n--- ZAUZEĆE MEMORIJE (mem) ---");
        System.out.println("MemoryManager je aktivan.");
    }
}