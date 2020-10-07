package mananger;

import model.memory.Memory;
import model.progress.PCB;
import os.OS;

public class MemoryManager {
    private Memory memory = OS.memory;

    public void allocateMemory(byte[] program) throws Exception {
        memory.allocate(program);
    }//分配内存的方法，参数未知

    public void recoveredMemory(PCB pcb) {
        memory.release(pcb);
    }//回收内存的方法，参数未知
}
