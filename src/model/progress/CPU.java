package model.progress;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import mananger.DeviceManager;
import model.device.DeviceRequest;
import model.memory.*;
import os.OS;

public class CPU implements Runnable {
    static ReentrantLock lock = new ReentrantLock();
    //寄存器组
    private int AX; //0
    private int BX; //1
    private int CX; //2
    private int DX; //3
    private int PC;
    public char[] IR = new char[3];

    public static int PSW;
    public static final int NONE_INTERMIT = 0;  //无中断
    public static final int TIME_INTERMIT = 1;  //时间片到中断
    public static final int NORMAL_INTERMIT = 2;  //正常中断
    public static final int EQUIP_INTERMIT = 3;   //设备中断

    private int nextIR;
    private int OP;
    private int DR;
    private int SR;
    private String result = "NOP";
    private String  temp="NOP";


    private Memory memory;
    private DeviceManager deviceManager;

    public CPU() {
        this.memory = OS.memory;
        deviceManager = new DeviceManager(this);
    }

    /**
     * 初始化CPU
     */
    public void init() {
        String str = "NOP";
        IR = str.toCharArray();
        AX = 0;
        BX = 0;
        CX = 0;
        DX = 0;
        PC = 0;
        deviceManager.init();
    }

    /**
     * 运行转就绪态
     */
    public void Ready() {
        PCB pcb = memory.getRunningPCB();
        System.out.println("进程" + pcb.getPID() + "被放入就绪队列");
        memory.getWaitPCB().offer(pcb);
        pcb.setStatus(PCB.STATUS_WAIT);
    }

    /**
     * 将运行进程转换为阻塞态
     */
    public void block() {
        PCB pcb = memory.getRunningPCB();
        //修改进程状态
        pcb.setStatus(PCB.STATUS_BLOCK);
        //将进程链入对应的阻塞队列，然后转向进程调度
        memory.getBlockPCB().add(pcb);
    }

    /**
     * 进程调度,将进程从就绪态恢复到运行态
     */
    public void dispatch() {
        PCB pcb1 = memory.getRunningPCB();//当前运行的进程
        PCB pcb2 = memory.getWaitPCB().poll();//要运行的进程 （弹出队首 无则null）
        if (pcb2 == null) {//即 无要运行的元素
            pcb2 = memory.getRunningPCB();
        }

        //如果第一个就绪进程是闲逛进程且还有其他的就绪进程
        if (pcb2 == memory.getHangOutPCB() && memory.getWaitPCB().size() > 0) {
            memory.getWaitPCB().offer(pcb2);//将闲逛进程     插入队尾
            pcb2 = memory.getWaitPCB().poll();//队首弹出
        }

        memory.setRunningPCB(pcb2);//将当前运行进程设置为pcb2
        pcb2.setStatus(PCB.STATUS_RUN);//同时改pcb2的状态为运行态
        saveContext(pcb1);//保存现场
        recoveryContext(pcb2);//恢复现场
        System.out.println("要运行:" + pcb2.getPID());
    }

    /**
     * 进程唤醒
     */
    public void awake(PCB pcb) {
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
    private void saveContext(PCB pcb) {
        pcb.setCounter(PC);
        pcb.setAX(this.AX);
        pcb.setBX(this.BX);
        pcb.setCX(this.CX);
        pcb.setDX(this.DX);
    }

    /**
     * 恢复现场
     */
    private void recoveryContext(PCB pcb) {

        pcb.setStatus(PCB.STATUS_RUN);
        this.AX = pcb.getAX();
        this.BX = pcb.getBX();
        this.DX = pcb.getDX();
        this.CX = pcb.getCX();
        this.PC = pcb.getCounter();
    }

    /**
     * 进程撤销
     */
    public void destroy() {
        PCB pcb = memory.getRunningPCB();
        System.out.println("进程" + pcb.getPID() + "运行结束,撤销进程");
        memory.release(pcb);
        /*回收进程所占内存*/
        /*SubArea subArea = null;
        List<SubArea> subAreas = memory.getSubAreas();
        for (SubArea s : subAreas) {
            if (s.getTaskNumber() == pcb.getPID()) {//找到那个进程
                subArea = s;
                break;
            }
        }
        subArea.setStatus(SubArea.STATUS_FREE);//找到后改状态为free
        int index = subAreas.indexOf(subArea);

        //如果不是第一个，判断上一个分区是否为空闲
        if (index > 0) {
            SubArea preSubArea = subAreas.get(index - 1);
            if (preSubArea.getStatus() == SubArea.STATUS_FREE) {//合并空闲区
                preSubArea.setSize(preSubArea.getSize() + subArea.getSize());
                subAreas.remove(subArea);
                subArea = preSubArea;//一定要有这一句 不然如果下一个是空闲区 合并会出错
            }
        }
        //如果不是最后一个，判断下一个分区是否空闲
        if (index < subAreas.size() - 1) {
            SubArea nextSubArea = subAreas.get(index + 1);
            if (nextSubArea.getStatus() == SubArea.STATUS_FREE) {//合并空闲区
                nextSubArea.setSize(nextSubArea.getSize() + subArea.getSize());
                nextSubArea.setStartAdd(subArea.getStartAdd());
                subAreas.remove(subArea);
            }
        }*/


    }

    /**
     * 取指令
     */
    public void fetchInstruction() {
        if (memory.getRunningPCB() == memory.getHangOutPCB()) {
            //IR=0;//NOP不执行
        } else {
            String[] userArea = memory.getUserArea();
            IR = userArea[PC].toCharArray();
            PC++;
        }
        //    System.out.println("取指完成，开始运行指令"+IR);
    }


    /**
     * 执行与写回
     */
    public String execute() {
        result = "hangdOutProcess......";
        PSW=CPU.NORMAL_INTERMIT;
        if (IR[0] == 'e' && IR[1] == 'n' && IR[2] == 'd') {
            PSW = CPU.NORMAL_INTERMIT;
            destroy();    //END
            dispatch();
            temp += "end";
            result += "end";
            return result;
        } else if (IR[0] == '!') {
            try {
                result += "!" + IR[1] + IR[2];
                PSW = CPU.EQUIP_INTERMIT;;
                DeviceRequest deviceRequest = new DeviceRequest();
                temp+="申请"+IR[1]+IR[2]+".....";
                deviceRequest.setDeviceName(temp);
                deviceRequest.setWorkTime((int)(Math.random()*5000));
                deviceRequest.setPcb(memory.getRunningPCB());
                //阻塞进程
                block();
                dispatch();

            } catch (Exception e) {
                PSW = CPU.EQUIP_INTERMIT;        //设备中断
                e.printStackTrace();

            }
            return result;
        } else if (IR[1] == '=') {
            result += "x=" + IR[2]+"（赋值操作）";
            temp += "x="+IR[2];
            String rest = String.valueOf(IR[2]);
            AX = Integer.parseInt(rest);
            return result;
        } else if (IR[1] == '+') {
            String rest = String.valueOf(IR[2]);
            int c = Integer.parseInt(rest);
            AX = AX + c;
            temp += "x="+ AX;
            result += "x+" + IR[2];
            return result;

        } else if (IR[1] == '-') {
            String rest = String.valueOf(IR[2]);
            int c = Integer.parseInt(rest);
            AX = AX - c;
            temp +=  "x="+AX;
            result += "x-" + IR[2];
            return result;
        }
        return "the instruction is false";
    }



    @Override
    public void run() {
        //这边可能由有点小bug 看到时候最后的运行的调试
        while (OS.launched) {
            try {
                Thread.sleep(Clock.TIMESLICE_UNIT);
            } catch (InterruptedException e) {
                return;
            }

            if(PSW==CPU.NORMAL_INTERMIT)return;
            if(CPU.PSW==CPU.TIME_INTERMIT)return;
            else if(PSW==CPU.EQUIP_INTERMIT){
                return;
            }
            lock.lock();
            try {
                fetchInstruction();//取指
                execute();//执行
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            if(CPU.PSW==CPU.EQUIP_INTERMIT) return ;

            if(OS.clock.getRestTime()==0&&CPU.PSW!=CPU.NORMAL_INTERMIT)
                CPU.PSW=CPU.TIME_INTERMIT;


        }
    }

    public String getResultOfProcess() {
        String ResultOfProcess;
        lock.lock();
        ResultOfProcess=temp;
        lock.unlock();
        return ResultOfProcess;
    }

    public String getInstuction(){

        String instuction;
        lock.lock();
        instuction=result;
        lock.unlock();
        return instuction;

    }

}
