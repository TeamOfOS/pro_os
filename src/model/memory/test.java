package model.memory;

import java.util.List;

public class test {
    public static void main(String[] args) {
        PCB pcb = new PCB();
        pcb.allocatePCB(5);
        pcb.allocatePCB(9);
        pcb.allocatePCB(8);
        pcb.allocatePCB(6);
        pcb.allocatePCB(1);
        pcb.allocatePCB(2);
        pcb.allocatePCB(3);
        pcb.allocatePCB(4);
        pcb.allocatePCB(7);
        pcb.allocatePCB(10);
        pcb.allocatePCB(11);
        List<Process> list = pcb.getList();
        for(Process p : list){
            System.out.print(p.getProcessId()+" ");
        }

        pcb.recoveryPCB(2);
        pcb.allocatePCB(11);
        System.out.println();
        for(Process p : list){
            System.out.print(p.getProcessId()+" ");
        }


    }
}