package model.progress;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import controller.contextController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import mananger.DeviceManager;
import model.device.DeviceRequest;
import model.memory.*;
import os.OS;

public class CPU implements Runnable {
    @FXML
    private TextArea processRunningView;
    @FXML
    private TextArea processResultView;
    static ReentrantLock lock = new ReentrantLock();
    //寄存器组
    private int AX; //0
    private int BX; //1
    private int CX; //2
    private int DX; //3
    private int PC;
    public String IR;

    public static int PSW;
    public static final int NONE_INTERMIT = 0;  //无中断
    public static final int TIME_INTERMIT = 1;  //时间片到中断
    public static final int NORMAL_INTERMIT = 2;  //正常中断
    public static final int EQUIP_INTERMIT = 3;   //设备中断

    private int nextIR;
    private int OP;
    private int DR;
    private int SR;
    private String result = "hangdOutProcess......"+"\n";
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
        IR = "null";
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
           // System.out.println("是不是这个原因");
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
        //System.out.println("进入方法");
        if (memory.getRunningPCB() == memory.getHangOutPCB()) {
            IR = "";
            dispatch();
            //IR=0;//NOP不执行
            System.out.println("进入空闲进程");
        } else {
            //memory.setRunningPCB(memory.getWaitPCB().peek());
            System.out.println("变成闲逛进程了吗"+(memory.getRunningPCB() == memory.getHangOutPCB()));
            String[] userArea = memory.getUserArea();
/*            char[] charIR = new char[4];
            int indexOfChar =0;
            for (int i=PC;i<PC+3;i++){
                //charIR[indexOfChar] = userArea[i];

            }*/
            IR = userArea[PC]+userArea[PC+1]+userArea[PC+2]+userArea[PC+3];
            PC+=4;
            System.out.println("进入不空闲进程");
            System.out.println("PC当前位置："+PC);
            System.out.println("userArea[PC]的值："+userArea[PC]);
            System.out.println("IR当前值："+IR);
        }
System.out.println("我用了这个方法吗");
        //    System.out.println("取指完成，开始运行指令"+IR);
    }


    /**
     * 执行与写回
     */
    public String execute() {
        temp = "NOP";
        //修改了result
        result = "hangdOutProcess......"+"\n";
        PSW=CPU.NORMAL_INTERMIT;
        System.out.println("准备开始运行");
        if (IR.contains("end")) {

            System.out.println("运行了end");
            PSW = CPU.NORMAL_INTERMIT;
            destroy();    //END
            dispatch();
            temp = "end";
            result =IR;
            return result;
        } else if (IR.contains("!")) {
            try {
                System.out.println("运行了！");
                result = IR;
                PSW = CPU.EQUIP_INTERMIT;;
                DeviceRequest deviceRequest = new DeviceRequest();
                temp="申请"+IR.charAt(1)+IR.charAt(2)+".....";
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
        } else if (IR.contains("=")) {
            System.out.println("运行了==");
            result = IR;

            String rest = String.valueOf(IR.charAt(2));
            AX = Integer.parseInt(rest);
            temp ="x="+AX;
            //
            PSW = NONE_INTERMIT;
            return result;
        } else if (IR.contains("+") ) {
            System.out.println("运行了+");
            //String rest = String.valueOf(IR.charAt(2));
            //int c = Integer.parseInt(rest);
            //AX = AX + c;
            AX = AX + 1;
            temp ="x="+ AX;
            result =IR;
            //
            PSW = NONE_INTERMIT;
            return result;

        } else if (IR.contains("-")) {
            System.out.println("运行了-");
/*            String rest = String.valueOf(IR.charAt(2));
            int c = Integer.parseInt(rest);
            AX = AX - c;*/
            AX = AX - 1;
            temp ="x="+AX;
            result=IR;
            //
            PSW = NONE_INTERMIT;
            return result;
        }
        System.out.println("啥也莫得运行");
        //修改
        PSW = NONE_INTERMIT;
        return "the instruction is false";
    }



    @Override
    public void run() {
        System.out.println("cpu run了嘛");
        //这边可能由有点小bug 看到时候最后的运行的调试
        while (OS.launched) {
            System.out.println("cpu run了嘛1");
            try {
                System.out.println("cpu run了嘛2");
                Thread.sleep(Clock.TIMESLICE_UNIT);
                System.out.println("cpu run了嘛3");

            } catch (InterruptedException e) {
                return;
            }
System.out.println("过来了嘛");
            if(PSW==CPU.NORMAL_INTERMIT)
            {
                System.out.println("结束1");
                continue;
                //return;
            }
            if(CPU.PSW==CPU.TIME_INTERMIT){
                System.out.println("结束2");
                result="时间片中断了！！"+"\n";
                temp="时间片中断了！！";
                continue;
                //return;
            }
            else if(PSW==CPU.EQUIP_INTERMIT){
                System.out.println("结束3");
                result="设备中断了！！"+"\n";
                temp="设备中断了！！";
                continue;
                //return;
            }

            lock.lock();
            try {
System.out.println("取指1");
                fetchInstruction();//取指
                System.out.println("取指2");
                execute();//执行
                if(CPU.PSW==CPU.TIME_INTERMIT){
                    System.out.println("结束2");
                    result="时间片中断了！！"+"\n";
                    temp="时间片中断了！！";

                }
                else if(PSW==CPU.EQUIP_INTERMIT){
                    System.out.println("结束3");
                   // result="设备中断了！！"+"\n";
                    temp="设备中断了！！";

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                lock.unlock();
            }



            if(CPU.PSW==CPU.EQUIP_INTERMIT)
            {
                continue;
                //return ;
            }

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

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

}
