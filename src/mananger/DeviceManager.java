package mananger;

import model.progress.CPU;

public  class  DeviceManager {
    private CPU cpu;

    public DeviceManager(CPU cpu){
        this.cpu=cpu;
    }//构造方法

    public void  init(){

    }//初始化

    public static void allocateDevice() {

    }//分配设备的方法，参数未知

    public static void recoveredDevice() {

    }//回收设备的方法，参数未知
}
