package Projekat;

public abstract class FsNode {
    protected String name;
    protected Directory parent;

    public FsNode(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public String getPath() {
        if (parent == null || parent.getName().equals("/")) {
            return "/" + name;
        }
        return parent.getPath() + "/" + name;
    }
}