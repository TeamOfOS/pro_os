package model.device;

import model.progress.PCB;

public class DeviceRequest {
    private PCB pcb;
    //占用时间，以毫秒为单位
    private long workTime;
    //剩余时间 以个位为单位
    private int leftTime;
    //设备名称
    private String deviceName;
    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(int leftTime) {
        this.leftTime = leftTime;
    }
}
