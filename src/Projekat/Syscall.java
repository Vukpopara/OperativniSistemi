package Projekat;

public class Syscall {
    private final SyscallType type;
    private final int processId;
    private final Object parameters;

    public Syscall(SyscallType type, int processId, Object parameters) {
        this.type = type;
        this.processId = processId;
        this.parameters = parameters;
    }

    public SyscallType getType() {
        return type;
    }

    public int getProcessId() {
        return processId;
    }

    public Object getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "Syscall{" +
                "type=" + type +
                ", processId=" + processId +
                ", parameters=" + parameters +
                '}';
    }
}