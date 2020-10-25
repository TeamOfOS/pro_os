package model.device;

import model.progress.PCB;
import java.util.concurrent.TimeUnit;
import model.device.DelayItem;

public class DeviceOccupy extends DelayItem<PCB> {
    private String deviceName;
    public DeviceOccupy(PCB obj, long workTime, TimeUnit timeUnit) {
        super(obj, workTime, timeUnit);
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
