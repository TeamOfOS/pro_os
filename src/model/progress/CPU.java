package model.progress;
import java.util.concurrent.locks.ReentrantLock;

import mananger.DeviceManager;
import model.memory.*;
import os.OS;

public class CPU {
    static ReentrantLock lock = new ReentrantLock();
    //寄存器组
    private int IR;
    private int AX; //0
    private int BX; //1
    private int CX; //2
    private int DX; //3
    private int PC;

    private int nextIR;
    private int OP;
    private int DR;
    private int SR;
    private String result="NOP";


    private Memory memory;
    private DeviceManager deviceManager;
    public CPU() {
        this.memory = OS.memory;
        deviceManager=new DeviceManager(this);
    }

    /**
     * 初始化CPU
     */
    public void init(){
        IR=0;
        AX=0;
        BX=0;
        CX=0;
        DX=0;
        PC=0;
        deviceManager.init();
    }

    /**
     *运行转就绪态
     */
    public void Ready(){
        PCB pcb=memory.getRunningPCB();
        System.out.println("进程"+pcb.getPID()+"被放入就绪队列");
        memory.getWaitPCB().offer(pcb);
        pcb.setStatus(PCB.STATUS_WAIT);
    }

    /**
     * 进程调度,将进程从就绪态恢复到运行态
     */
    public void dispatch() {
        PCB pcb1= memory.getRunningPCB();//当前运行的进程
        PCB pcb2=memory.getWaitPCB().poll();//要运行的进程 （弹出队首 无则null）
        if (pcb2==null){//即 无要运行的元素
            pcb2=memory.getRunningPCB();
        }

        //如果第一个就绪进程是闲逛进程且还有其他的就绪进程
        if (pcb2==memory.getHangOutPCB()&&memory.getWaitPCB().size()>0){
            memory.getWaitPCB().offer(pcb2);//将闲逛进程     插入队尾
            pcb2=memory.getWaitPCB().poll();//队首弹出
        }

        memory.setRunningPCB(pcb2);
        pcb2.setStatus(PCB.STATUS_RUN);
        //保存现场
        saveContext(pcb1);
        //恢复现场
        recoveryContext(pcb2);
        System.out.println("要运行:"+pcb2.getPID());
    }


    /**
     * 保存上下文
     */
    private void  saveContext(PCB pcb){
        //   System.out.println("保留现场");
        pcb.setCounter(PC);
        pcb.setAX(this.AX);
        pcb.setBX(this.BX);
        pcb.setCX(this.CX);
        pcb.setDX(this.DX);
    }

    /**
     * 恢复现场
     */
    private void recoveryContext(PCB pcb){
        //      System.out.println("恢复现场");
        pcb.setStatus(PCB.STATUS_RUN);
        this.AX=pcb.getAX();
        this.BX=pcb.getBX();
        this.DX=pcb.getDX();
        this.CX=pcb.getCX();
        this.PC=pcb.getCounter();
    }



}
