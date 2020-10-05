package model.memory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 内存空间分配表
 */
public class MemorySpaceAllocationTable {
    //空闲区链表
    private List<FreeBlock> list = new ArrayList<>();

    /**
     * 初始化空闲区链表
     * @param length  空闲区长度
     * @param startIndex  空闲区起址
     */
    public void initialize(int length,int startIndex){
        FreeBlock fb = new FreeBlock(startIndex,length);
        list.add(fb);
    }

    /**
     * 首次适配法为进程分配内存
     * @param processSize 进程大小
     * @return 返回true-->分配成功，返回false-->分配失败
     */
    public boolean firstFit(int processSize){
        Iterator<FreeBlock> iterator = list.iterator();
        while(iterator.hasNext()){
            FreeBlock freeblock = iterator.next();
            //如果空闲区大小 大于等于 进程大小时，分配成功并改变空闲区链表
            if(freeblock.getLength() >= processSize){
                //改变空闲区链表
                freeblock.setStartIndex(freeblock.getStartIndex()+processSize);
                freeblock.setLength(freeblock.getLength()-processSize);
                //分配成功
                return true;
            }
        }
        return false;
    }

    /**
     * 回收
     * @param startIndex 回收块起始地址
     * @param length 回收块长度
     */
    public void recovery(int startIndex,int length){
        //检查可能出现合并的情况：
        //1、回收块的起始地址等于空闲区链表中某空闲块的终址
        //2、回收块的终址等于空闲区链表中某空闲区的起始地址
        Iterator<FreeBlock> iterator = list.iterator();
        while(iterator.hasNext()){
            FreeBlock fb = iterator.next();
            if(fb.getEndIndex() == startIndex){
                //回收块的起始地址等于空闲区链表中某空闲块的终址
                fb.setLength(fb.getLength()+length);
                return;
            }else if(startIndex+length == fb.getStartIndex()){
                //回收块的终址等于空闲区链表中某空闲区的起始地址
                fb.setStartIndex(fb.getStartIndex()-length);
                fb.setLength(fb.getLength()+length);
                return;
            }
        }
        FreeBlock freeBlock = new FreeBlock(startIndex,length);
        list.add(freeBlock);
        //按照起址升序排序
        list.sort(new Comparator<FreeBlock>() {
            @Override
            public int compare(FreeBlock o1, FreeBlock o2) {
                return o1.getStartIndex() - o2.getStartIndex();
            }
        });
    }

    /**
     * 测试方法
     * @return
     */
    public List<FreeBlock> getList(){
        return list;
    }
}