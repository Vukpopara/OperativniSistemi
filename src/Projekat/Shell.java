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
        System.out.println("\n=== OPERATIVNI SISTEM - KOMANDNI INTERFEJS ===");
        System.out.println("Unesite 'pomoc' ili 'help' za prikaz spiska svih komandi.\n");

        while (running) {
            System.out.print("OS_CLI:" + getFullPath(currentDir) + "> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();

            switch (command) {
                case "procesi":
                case "ps":
                    listProcesses();
                    break;

                case "pokreni":
                case "run":
                    if (parts.length > 1) {
                        int priority = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;
                        kernel.createProcess(parts[1], priority);
                    } else {
                        System.out.println("Upotreba: pokreni <naziv_procesa> [prioritet]");
                    }
                    break;

                case "memorija":
                case "mem":
                    showMemory();
                    break;

                case "kreiraj_folder":
                case "mkdir":
                    if (parts.length > 1) {
                        fileSystem.createDirectory(getFullPath(currentDir) + parts[1]);
                        System.out.println("[Sistem]: Uspjesno kreiran folder '" + parts[1] + "'");
                    } else {
                        System.out.println("Upotreba: kreiraj_folder <naziv_foldera>");
                    }
                    break;

                case "kreiraj_fajl":
                case "touch":
                case "create":
                    if (parts.length > 1) {
                        fileSystem.createFile(getFullPath(currentDir) + parts[1]);
                    } else {
                        System.out.println("Upotreba: kreiraj_fajl <naziv_fajla>");
                    }
                    break;

                case "sadrzaj":
                case "dir":
                case "ls":
                    showDirectoryContent();
                    break;

                case "otvori_folder":
                case "cd":
                    if (parts.length > 1) {
                        changeDirectory(parts[1]);
                    } else {
                        System.out.println("Upotreba: otvori_folder <naziv_foldera>");
                    }
                    break;

                case "obrisi":
                case "rm":
                    if (parts.length > 1) {
                        fileSystem.delete(getFullPath(currentDir) + parts[1]);
                        System.out.println("[Sistem]: Obrisan element '" + parts[1] + "'");
                    } else {
                        System.out.println("Upotreba: obrisi <naziv_elementa>");
                    }
                    break;

                case "prenos_podataka":
                case "dma":
                    if (parts.length > 3) {
                        int block = Integer.parseInt(parts[1]);
                        int ramAddr = Integer.parseInt(parts[2]);
                        int size = Integer.parseInt(parts[3]);
                        kernel.startDMATransfer(block, ramAddr, size);
                    } else {
                        System.out.println("Upotreba: prenos_podataka <blok_diska> <ram_adresa> <velicina>");
                    }
                    break;

                case "izlaz":
                case "exit":
                    running = false;
                    System.out.println("Zatvaranje OS Shell-a...");
                    break;

                case "pomoc":
                case "help":
                    printHelp();
                    break;

                default:
                    System.out.println("Nepoznata komanda: '" + command + "'. Ukucajte 'pomoc' za spisak komandi.");
                    break;
            }
        }
    }

    private void printHelp() {
        System.out.println("\n====================== SPISAK KOMANDI ======================");
        System.out.println("  [Upravljanje procesima]");
        System.out.println("    procesi  / ps               - Prikaz svih aktivnih procesa");
        System.out.println("    pokreni  / run <naziv>      - Pokretanje novog procesa");
        System.out.println("    memorija / mem              - Prikaz zauzeća RAM memorije");
        System.out.println("\n  [Rad sa fajlovima i folderima]");
        System.out.println("    sadrzaj  / dir / ls         - Prikaz sadržaja trenutnog foldera");
        System.out.println("    kreiraj_folder / mkdir <n>  - Pravljenje novog foldera");
        System.out.println("    kreiraj_fajl / touch <n>    - Pravljenje novog fajla sa alokacijom diska");
        System.out.println("    otvori_folder / cd <p>      - Ulazak u folder (koristite '..' za nazad)");
        System.out.println("    obrisi / rm <naziv>         - Brisanje fajla ili foldera");
        System.out.println("\n  [Hardver & DMA]");
        System.out.println("    prenos_podataka / dma       - Pokretanje DMA prenosa diska u RAM");
        System.out.println("\n  [Sistem]");
        System.out.println("    izlaz / exit                - Gašenje interfejsa");
        System.out.println("============================================================\n");
    }

    private void showDirectoryContent() {
        System.out.println("\n------------------------------------------------");
        System.out.printf("%-12s %-20s\n", "TIP ELEMENTA", "NAZIV");
        System.out.println("------------------------------------------------");
        if (currentDir.getChildrenMap().isEmpty()) {
            System.out.println("  (Folder je trenutno prazan)");
        } else {
            for (FsNode node : currentDir.getChildrenMap().values()) {
                String type = (node instanceof Directory) ? "[FOLDER]" : "[FAJL]";
                System.out.printf("%-12s %-20s\n", type, node.getName());
            }
        }
        System.out.println("------------------------------------------------\n");
    }

    private void changeDirectory(String path) {
        if (path.equals("..")) {
            if (currentDir.getParent() != null) {
                currentDir = currentDir.getParent();
            } else {
                System.out.println("[Sistem]: Već se nalazite u početnom (root) folderu.");
            }
        } else {FsNode next = currentDir.getChild(path);
            if (next instanceof Directory) {
                currentDir = (Directory) next;
            } else {
                System.out.println("[Greška]: Folder ne postoji -> " + path);
            }
        }
    }

    private String getFullPath(Directory dir) {
        if (dir.getParent() == null) return "/";
        return getFullPath(dir.getParent()) + dir.getName() + "/";
    }

    private void listProcesses() {
        System.out.println("\n--- TRENUTNO AKTIVNI PROCESI ---");
        if (kernel.getProcessTable().isEmpty()) {
            System.out.println("Nema aktivnih procesa.");
        } else {
            for (PCB p : kernel.getProcessTable()) {
                System.out.println("PID: " + p.getPid() + " | Stanje: " + p.getState() + " | Prioritet: " + p.getPriority());
            }
        }
        System.out.println("--------------------------------\n");
    }

    private void showMemory() {
        System.out.println("\n--- ZAUZEĆE RAM MEMORIJE ---");
        System.out.println("Ukupan RAM: " + kernel.getMemoryManager().getTotalSize() + " B");
        System.out.println("Status: Upravljanje memorijom je aktivno.");
        System.out.println("----------------------------\n");
    }
}