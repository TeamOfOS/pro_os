package model.memory;

public class Process {
    private int processId;

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public Process(int processId) {
        this.processId = processId;
    }

    @Override
    public String toString() {
        return "Process{" +
                "processId=" + processId +
                '}';
    }
}