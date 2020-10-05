package model.memory;

/**
 * 用户区
 */
public class UserArea {
    private static final int MAXLENGGH = 512; //用户区的最大长度
    private MemorySpaceAllocationTable memorySpaceAllocationTable = new MemorySpaceAllocationTable(); //内存空间分配表

    public UserArea(){
        memorySpaceAllocationTable.initialize(512,30); //内存中前30个字节为系统区
    }

    /**
     * 请求分配指定大小的内存
     * @param processSize 进程所需内存大小
     * @return ture-->分配成功，false-->分配失败进入等待
     */
    public boolean requestDispatching(int processSize){
        return memorySpaceAllocationTable.firstFit(processSize);
    }

    /**
     * 请求回收
     * @param startIndex 起址
     * @param length 长度
     */
    public void release(int startIndex,int length){
        memorySpaceAllocationTable.recovery(startIndex,length);
    }
}