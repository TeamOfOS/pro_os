package ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DeviceVo {
    private SimpleStringProperty deviceName;
    private SimpleStringProperty PID;
    private SimpleIntegerProperty leftTime;
    //等待对列
    public DeviceVo(String deviceName,int PID){
        this.deviceName=new SimpleStringProperty(deviceName);
        this.PID=new SimpleStringProperty(PID+"");
    }
    //使用队列
    public DeviceVo(String deviceName,int PID,int leftTime){
        this.deviceName=new SimpleStringProperty(deviceName);
        this.PID=new SimpleStringProperty(PID+"");
        this.leftTime = new SimpleIntegerProperty(leftTime);
    }
    public String getDeviceName() {
        return deviceName.get();
    }

    public SimpleStringProperty deviceNameProperty() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName.set(deviceName);
    }

    public String getPID() {
        return PID.get();
    }

    public SimpleStringProperty PIDProperty() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID.set(PID);
    }

    public int getLeftTime() {
        return leftTime.get();
    }

    public SimpleIntegerProperty leftTimeProperty() {
        return leftTime;
    }

    public void setLeftTime(int leftTime) {
        this.leftTime.set(leftTime);
    }
}
