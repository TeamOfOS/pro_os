package model.memory;
import model.progress.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Memory {

    private byte[] userArea; //用户区内存

    private List<SubArea> subAreas;//内存分配数组

    private Queue<PCB> waitPCB; //就绪进程

    private Queue<PCB> blockPCB; //阻塞进程

    private  PCB runningPCB;//运行进程（只有1个）

    private PCB hangOutPCB; //闲逛进程（就绪队列为空时运行闲逛进程）

    public byte[] getUserArea() {
        return userArea;
    }

    public List<SubArea> getSubAreas() {
        return subAreas;
    }

    public void setSubAreas(List<SubArea> subAreas) {
        this.subAreas = subAreas;
    }

    public PCB getRunningPCB() {

        return runningPCB;
    }

    public void setRunningPCB(PCB runningPCB) {

        this.runningPCB = runningPCB;
    }

    public PCB getHangOutPCB() {

        return hangOutPCB;
    }

    public Queue<PCB> getWaitPCB() {

        return waitPCB;
    }

    public void setWaitPCB(Queue<PCB> waitPCB) {

        this.waitPCB = waitPCB;
    }

    public Queue<PCB> getBlockPCB() {

        return blockPCB;
    }

    public void setBlockPCB(Queue<PCB> blockPCB) {

        this.blockPCB = blockPCB;
    }

    public List<PCB> getAllPCB() {
        List<PCB> allPCB=new ArrayList<>(10);
        allPCB.add(runningPCB);
        allPCB.addAll(blockPCB);
        allPCB.addAll(waitPCB);
        return allPCB;
    }
}
