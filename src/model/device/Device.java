package model.device;

import java.util.concurrent.atomic.AtomicInteger;

public class Device {
    public static final int STATUS_FREE=0;//设备空闲
    public static final int STATUS_BUSY=1;//设备被占用
    //设备状态
    private int status;
    //占用时间
    private int elapsedTime;
    //设备名称
    protected String name;
    //设备数量
    private volatile AtomicInteger count;

    public Device(int count){
        this.count=new AtomicInteger(count);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count.intValue();
    }

    public void increaseCount(){
        count.getAndIncrement();
    }

    public int decreaseCount(){
        return count.getAndDecrement();
    }

    public void setCount(int count) {
        this.count.set(count);
    }
}
