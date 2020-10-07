package model.memory;

/**
 * 分区
 */
public class SubArea {

    public  static final int STATUS_FREE=0;//分区空闲
    public  static final int STATUS_BUSY=1;//分区被使用

    private int startIndex;//分区开始地址
    private int size;//分区大小
    private int status;//分区当前状态
    private int taskNumber;//作业号


    public int getStartAdd() {
        return startIndex;
    }

    public void setStartAdd(int startAdd) {
        this.startIndex = startAdd;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

}
