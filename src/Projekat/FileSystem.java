package Projekat;

public class FileSystem {
    private Directory root;
    private DiskDevice disk;

    public FileSystem() {
        this.root = new Directory("/", null);
        this.disk = new DiskDevice("Disk0");
    }

    public File createFile(String path) {

        File file = new File(path, root);
        root.addChild(file);
        return file;
    }

    public Directory createDirectory(String path) {

        Directory dir = new Directory(path, root);
        root.addChild(dir);
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

        System.out.println("Obrisan čvor na putanji: " + path);
    }

    public FsNode resolve(String path) {
        if (path == null || path.equals("/")) {
            return root;
        }
        return root.getChild(path);
    }

    public Directory getRoot() {
        return root;
    }
}