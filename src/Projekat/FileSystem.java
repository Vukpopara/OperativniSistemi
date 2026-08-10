package Projekat;

public class FileSystem {
    private Directory root;
    private DiskDevice disk;

    public FileSystem() {
        this.root = new Directory("/", null);
        this.disk = new DiskDevice("Disk0");
    }

    public File createFile(String path) {
        String[] parts = path.split("/");
        Directory parentDir = navigateToDirectory(parts);
        String fileName = parts[parts.length - 1];

        File file = new File(fileName, parentDir);
        parentDir.addChild(file);

        if (disk != null) {
            disk.allocateFileSpace(file);
        }
        return file;
    }

    public Directory createDirectory(String path) {
        String[] parts = path.split("/");
        Directory parentDir = navigateToDirectory(parts);
        String dirName = parts[parts.length - 1];

        Directory dir = new Directory(dirName, parentDir);
        parentDir.addChild(dir);
        return dir;
    }

    public OpenFileHandle open(String path) {
        FsNode node = resolve(path);
        if (node instanceof File) {
            return new OpenFileHandle((File) node, FileMode.READ);
        }
        return null;
    }

    public void delete(String path) {
        FsNode node = resolve(path);
        if (node != null && node.getParent() != null) {
            node.getParent().getChildrenMap().remove(node.getName());
            if (node instanceof File && disk != null) {
                disk.freeFileSpace((File) node);
            }
            System.out.println("Obrisan čvor na putanji: " + path);
        }
    }

    public FsNode resolve(String path) {
        if (path == null || path.equals("/") || path.isEmpty()) {
            return root;
        }

        String[] parts = path.split("/");
        Directory current = root;

        for (String part : parts) {
            if (part.isEmpty()) continue;
            FsNode child = current.getChild(part);
            if (child == null) return null;

            if (child instanceof Directory) {
                current = (Directory) child;
            } else {
                return child;
            }
        }
        return current;
    }

    private Directory navigateToDirectory(String[] parts) {
        Directory current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;

            FsNode next = current.getChild(part);
            if (next == null) {

                 next = new Directory(part, current);
                current.addChild(next);
            }
            if (next instanceof Directory) {
                current = (Directory) next;
            }
        }
        return current;
    }

    public Directory getRoot() {
        return root;
    }

    public DiskDevice getDisk() {
        return disk;
    }
}