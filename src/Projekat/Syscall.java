package Projekat;

import java.util.ArrayList;
import java.util.List;

public class Syscall {
    private SyscallType type;
    private List<String> args;

    public Syscall(SyscallType type) {
        this.type = type;
        this.args = new ArrayList<>();
    }

    public Syscall(SyscallType type, List<String> args) {
        this.type = type;
        this.args = args != null ? args : new ArrayList<>();
    }

    public SyscallType getType() {
        return type;
    }

    public List<String> getArgs() {
        return args;
    }
}