package model.progress;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import mananger.DeviceManager;
import model.memory.*;
import os.OS;

public class CPU implements Runnable {
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

        memory.setRunningPCB(pcb2);//将当前运行进程设置为pcb2
        pcb2.setStatus(PCB.STATUS_RUN);//同时改pcb2的状态为运行态
        saveContext(pcb1);//保存现场
        recoveryContext(pcb2);//恢复现场
        System.out.println("要运行:"+pcb2.getPID());
    }

    /**
     * 进程唤醒
     */
    public void awake(PCB pcb){
        lock.lock();
        // System.out.println("唤醒进程"+pcb.getPID());
        pcb.setStatus(PCB.STATUS_WAIT);  //将进程从阻塞队列中调入到就绪队列
        pcb.setEvent(PCB.EVENT_NOTING);
        memory.getBlockPCB().remove(pcb);//从阻塞队列中移除
        memory.getWaitPCB().add(pcb);//加入就绪队列
        lock.unlock();
    }

    /**
     * 保存上下文
     */
    private void  saveContext(PCB pcb){
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

        pcb.setStatus(PCB.STATUS_RUN);
        this.AX=pcb.getAX();
        this.BX=pcb.getBX();
        this.DX=pcb.getDX();
        this.CX=pcb.getCX();
        this.PC=pcb.getCounter();
    }

    /**
     * 进程撤销
     */
    public void destroy(){
        PCB pcb=memory.getRunningPCB();
        System.out.println("进程"+pcb.getPID()+"运行结束,撤销进程");
        /*回收进程所占内存*/
        SubArea subArea=null;
        List<SubArea> subAreas=memory.getSubAreas();
        for (SubArea s:subAreas){
            if (s.getTaskNumber()==pcb.getPID()){//找到那个进程
                subArea=s;
                break;
            }
        }
        subArea.setStatus(SubArea.STATUS_FREE);//找到后改状态为free
        int index=subAreas.indexOf(subArea);

        //如果不是第一个，判断上一个分区是否为空闲
        if (index>0){
            SubArea preSubArea=subAreas.get(index-1);
            if(preSubArea.getStatus()==SubArea.STATUS_FREE) {//合并空闲区
                preSubArea.setSize(preSubArea.getSize() + subArea.getSize());
                subAreas.remove(subArea);
                subArea = preSubArea;//一定要有这一句 不然如果下一个是空闲区 合并会出错
            }
        }
        //如果不是最后一个，判断下一个分区是否空闲
        if (index<subAreas.size()-1){
            SubArea nextSubArea=subAreas.get(index+1);
            if (nextSubArea.getStatus()==SubArea.STATUS_FREE) {//合并空闲区
                nextSubArea.setSize(nextSubArea.getSize() + subArea.getSize());
                nextSubArea.setStartAdd(subArea.getStartAdd());
                subAreas.remove(subArea);
            }
        }


    }

    /**
     * 取指令
     */
    public void fetchInstruction() {
        if (memory.getRunningPCB()==memory.getHangOutPCB()){
            IR=0;//NOP不执行
        }else{
            byte[] userArea = memory.getUserArea();
            IR = userArea[PC];
            PC++;
        }
        //    System.out.println("取指完成，开始运行指令"+IR);
    }

    /**
     * 译码
     */
    public void identifyInstruction(){

    }

    /**
     * 执行与写回
     */
    public void execute(){

    }

    @Override
    public void run() {
        while (OS.launched) {
            try {
                Thread.sleep(Clock.TIMESLICE_UNIT);
            } catch (InterruptedException e) {
                return;
            }
            lock.lock();
            try {
                fetchInstruction();//取指令
                identifyInstruction();//译码
                execute();//执行
                //  System.out.println("就绪队列队头进程："+memory.getWaitPCB().peek().getPID());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }


        }
    }
}
