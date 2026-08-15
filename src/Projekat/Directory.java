package Projekat;

import java.util.HashMap;
import java.util.Map;

public class Directory extends FsNode {
    private final Map<String, FsNode> childrenMap;

    public Directory(String name, Directory parent) {
        super(name, parent);
        this.childrenMap = new HashMap<>();
    }

    public Map<String, FsNode> getChildrenMap() {
        return childrenMap;
    }

    public void addChild(FsNode node) {
        if (node != null) {
            childrenMap.put(node.getName(), node);
            node.setParent(this);
        }
    }

    public FsNode getChild(String name) {
        return childrenMap.get(name);
    }

    public void removeChild(String name) {
        childrenMap.remove(name);
    }
}