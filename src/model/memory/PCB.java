package model.memory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 进程控制块（最多容纳十个）
 */
public class PCB {
    private static final int MAXCOUNTOFPCB = 10;
    private List<Process> arrayOfPCB = new ArrayList();
    private int occupiedPCB;//当前已被占用的PCB

    /**
     * 分配PCB
     * @return
     */
    public boolean allocatePCB(int processId){
        //当前PCB不满10个
        if(occupiedPCB < PCB.MAXCOUNTOFPCB){
            occupiedPCB++;
            arrayOfPCB.add(new Process(processId));
            return true;
        }
        return false;
    }

    /**
     * 回收PCB空间
     * @param processId
     */
    public void recoveryPCB(int processId){
        Iterator<Process> iterator = arrayOfPCB.iterator();
        Process tmp = null;
        while(iterator.hasNext()){
            Process next = iterator.next();
            if(next.getProcessId() == processId){
                tmp = next;
            }
        }
        if(tmp != null){
            arrayOfPCB.remove(tmp);
            occupiedPCB--;
        }
        return;
    }

    /**
     * 测试用
     * @return
     */
    public List<Process> getList(){
        return arrayOfPCB;
    }
}