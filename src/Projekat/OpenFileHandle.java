package Projekat;

public class OpenFileHandle {
    private final File file;
    private final FileMode mode;
    private int cursorPosition;

    public OpenFileHandle(File file, FileMode mode) {
        this.file = file;
        this.mode = mode;
        this.cursorPosition = 0;
    }

    public File getFile() {
        return file;
    }

    public FileMode getMode() {
        return mode;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        if (cursorPosition >= 0) {
            this.cursorPosition = cursorPosition;
        }
    }

    public void seek(int offset) {
        if (this.cursorPosition + offset >= 0) {
            this.cursorPosition += offset;
        }
    }

    @Override
    public String toString() {
        return "OpenFileHandle{" +
                "fileName='" + (file != null ? file.getName() : "null") + '\'' +
                ", mode=" + mode +
                ", cursorPosition=" + cursorPosition +
                '}';
    }
}