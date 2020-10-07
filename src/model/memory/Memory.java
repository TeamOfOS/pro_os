package model.memory;
import model.progress.*;

import java.util.*;


public class Memory {
    public static final int PCB_MAX_COUNT = 10;//内存中容纳的最大PCB数
    public static final int USERAREA_MAX_SIZE = 512;

    private String[] userArea; //模拟用户区内存的字节存储情况

    private List<SubArea> subAreas;//内存分配数组

    private Queue<PCB> waitPCB; //就绪进程

    private Queue<PCB> blockPCB; //阻塞进程

    private  PCB runningPCB;//运行进程（只有1个）

    private PCB hangOutPCB; //闲逛进程（就绪队列为空时运行闲逛进程）

    public Memory(){
        subAreas = Collections.synchronizedList(new LinkedList<>());
        waitPCB = new LinkedList<>();
        blockPCB = new LinkedList<>();
        hangOutPCB = new PCB();
        userArea = new String[USERAREA_MAX_SIZE];
        //内存参数初始化
       init();

    }

    public void init(){
        Arrays.fill(userArea,String.valueOf(0));
        waitPCB.removeAll(waitPCB);
        blockPCB.removeAll(blockPCB);
        subAreas.removeAll(subAreas);
        hangOutPCB.setStatus(PCB.STATUS_RUN);
        runningPCB=hangOutPCB;
        SubArea subArea = new SubArea();
        subArea.setSize(USERAREA_MAX_SIZE);
        subArea.setStartAdd(0);
        subArea.setStatus(SubArea.STATUS_FREE);
        subAreas.add(subArea);

    }
    /**
     * 给进程分配内存
     * @param program
     * @return 返回进程所占用的分区
     * @throws Exception
     */
    public SubArea allocate(byte[] program) throws Exception {
        //检查内存中PCB数量，最多容纳10个PCB
        if(getAllPCB().size() >= PCB_MAX_COUNT){
            throw new Exception("当前运行进程过多！");
        }
        //分配内存（首次适配）
        SubArea subArea = null;
        ListIterator<SubArea> iterator = getSubAreas().listIterator();
        while(iterator.hasNext()){
            SubArea tmp = iterator.next();
            //如果找到一个比进程字节大的空闲区域，则分配
            if(tmp.getStatus()==SubArea.STATUS_FREE&&tmp.getSize()>=program.length){
                subArea = tmp;
                break;
            }
        }
        if(subArea == null){
            throw new Exception("内存不足！");
        }
        PCB pcb = new PCB();
        if(subArea.getSize()>program.length){
            int newSize = subArea.getSize()-program.length;
            subArea.setSize(program.length);
            subArea.setStatus(SubArea.STATUS_BUSY);
            subArea.setTaskNumber(pcb.getPID());
            //分配了进程所需的区域后产生的新区域
            SubArea newSubArea = new SubArea();
            newSubArea.setStatus(SubArea.STATUS_FREE);
            newSubArea.setSize(newSize);
            newSubArea.setStartAdd(subArea.getStartAdd()+subArea.getSize());
            iterator.add(newSubArea);
        }else{
            //subArea.setSize(program.length);
            subArea.setStatus(SubArea.STATUS_BUSY);
            subArea.setTaskNumber(pcb.getPID());
        }
        //将数据存储到内存的用户区中
        for(int i = subArea.getStartAdd(),j=0;j<program.length;i++,j++){
            userArea[i] = String.valueOf(program[j]);
        }

        return subArea;
    }

    /**
     * 回收某进程占用的分区
     * @param pcb
     */
    public void release(PCB pcb){
        SubArea subArea = null;
        //找出该进程占用的分区
        for(SubArea s:getSubAreas()){
            if(s.getTaskNumber()==pcb.getPID()){
                subArea = s;
                break;
            }
        }
        subArea.setStatus(SubArea.STATUS_FREE);
        int index = subAreas.indexOf(subArea);
        //合并空闲分区
        if(index>0){
            SubArea preSubArea = subAreas.get(index-1);
            if(preSubArea.getStatus()==SubArea.STATUS_FREE){
                preSubArea.setSize(preSubArea.getSize()+subArea.getSize());
                subAreas.remove(subArea);
                subArea = preSubArea;
            }
        }
        index = subAreas.indexOf(subArea);
        if(index<subAreas.size()-1){
            SubArea nextSubArea = subAreas.get(index+1);
            if(nextSubArea.getStatus()==SubArea.STATUS_FREE){
                nextSubArea.setSize(nextSubArea.getSize()+subArea.getSize());
                nextSubArea.setStartAdd(subArea.getStartAdd());
                subAreas.remove(subArea);
            }
        }
    }
    /**
     * getter & setter
     * @return
     */
    public String[] getUserArea() {
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
