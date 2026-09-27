package Projekat;

import java.util.Scanner;

public class Shell {
    private OSKernel kernel;
    private FileSystem fileSystem;
    private boolean running;

    public Shell(OSKernel kernel, FileSystem fileSystem) {
        this.kernel = kernel;
        this.fileSystem = fileSystem;
        this.running = true;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Dobrodošli u OS Shell. Ukucajte 'help' ili 'pomoc' za pregled komandi.");

        while (running) {
            System.out.print("OS_CLI> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            switch (command) {
                case "help":
                case "pomoc":
                    printHelp();
                    break;

                case "ls":
                    fileSystem.printTree();
                    break;

                case "ps":
                    printProcessTable();
                    break;

                case "cat":
                    if (tokens.length > 1) {
                        fileSystem.readFile(tokens[1]);
                    } else {
                        System.out.println("Greška: Navedite putanju do fajla (npr. cat /home/demo.asm).");
                    }
                    break;

                case "pokreni_asm":
                    if (tokens.length > 1) {
                        kernel.executeAssemblyFile(tokens[1]);
                    } else {
                        System.out.println("Greška: Navedite putanju do .asm fajla (npr. pokreni_asm /home/demo.asm).");
                    }
                    break;

                case "clear":
                case "cls":
                    for (int i = 0; i < 50; i++) System.out.println();
                    break;

                case "exit":
                case "izlaz":
                    System.out.println("Gašenje Shell-a i sistema...");
                    running = false;
                    break;

                default:
                    System.out.println("Nepoznata komanda: '" + command + "'. Ukucajte 'help' za listu komandi.");
                    break;
            }
        }
    }

    private void printProcessTable() {
        System.out.println("\n--- TABELA PROCESA ---");
        for (PCB p : kernel.getProcessTable()) {
            System.out.println(p.toString());
        }
        System.out.println("----------------------\n");
    }

    private void printHelp() {
        System.out.println("\n--- DOSTUPNE KOMANDE ---");
        System.out.println("  ls                      - Prikazuje strukturu fajl sistema");
        System.out.println("  ps                      - Prikazuje tabelu aktivnih procesa");
        System.out.println("  cat <putanja>           - Ispisuje sadržaj fajla");
        System.out.println("  pokreni_asm <putanja>   - Prevedi i pokreni asemblerski fajl");
        System.out.println("  clear / cls             - Čisti ekran terminala");
        System.out.println("  exit / izlaz            - Izlaz iz sistema");
        System.out.println("------------------------\n");
    }
}