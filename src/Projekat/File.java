package Projekat;

public class File extends FsNode {
    private int size;

    public File(String name, Directory parent) {
        super(name, parent);
        this.size = 0;
    }

    public File(String name, Directory parent, int size) {
        super(name, parent);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + getName() + '\'' +
                ", path='" + getPath() + '\'' +
                ", size=" + size +
                '}';
    }
}