package os;

import application.Main;
import controller.ChangFileAttrController;
import controller.ChangeDirAttrController;
import controller.EditController;
import controller.contextController;
import javafx.stage.Stage;
import model.fileuser.OpenOperator;
import model.progress.CPU;
import model.progress.Clock;
import model.disk.Disk;
import model.memory.Memory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
公共变量类
 */
public class OS {

    public static Disk disk;//磁盘
    public static CPU cpu;//CPU
    public static Memory memory;//内存
    public static Clock clock;//时钟
    public static OS os;//os
    public static volatile boolean launched;//系统资源开关，volatile修饰 不进行系统优化，即每次都必须在内存中进行存取launched而不能在cache中读取
    public static OpenOperator openOperator;//打开的操作
    public contextController contextcontroller;//主要控制界面
    public ChangFileAttrController changFileAttrController;//改变文件属性界面
    public ChangeDirAttrController changeDirAttrController;//改变目录属性界面
    public EditController editController;//打开编辑界面

    public static final int PROCESS_MAX=10;//最大进程数

    static {
        try {
            initDisk();
            disk = new Disk();
            memory = new Memory();
            cpu = new CPU();
            clock = new Clock();
            openOperator = new OpenOperator();

            os=new OS();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {
        cpu.init();
        memory.init();
        clock.init();
        //fileOperator.init();
    }


    private OS() throws Exception {
    }

    public static synchronized  OS getInstance( ) throws Exception {
        return os;
    }

    public void start() throws Exception {
        //TODO
        init();
        new Thread(cpu).start();
        new Thread(clock).start();

    }

    public void close() {
        launched = false;
    }

    /*
    磁盘初始化
     */

    static void initDisk() throws IOException {
        File file = new File("disk.dat");
        FileOutputStream fout = null;
        //判断模拟磁盘是否创建
        if(file.exists()){
            try{
                fout = new FileOutputStream(file);
                byte[] bytes;
                bytes = new byte[64*128];
                for(int i=0;i<128;i++){

                    //写入初始分配表
/*                    for(int j=0;j<64;j++){
                        bytes[j*128+i] =0;
                    }*/
                    if (i==0){
                        bytes[0]=-1;
                        bytes[1]=-1;
                        //bytes[2]=-1;
                    }

                    //写入根目录和生成的随机文件


                }
                fout.write(bytes);

            } catch (FileNotFoundException e) {
                java.lang.System.out.println("打开/新建磁盘文件失败！");
                e.printStackTrace();
            } catch (IOException e) {
                java.lang.System.out.println("写入文件时发生错误");
                e.printStackTrace();
                java.lang.System.exit(0);
            } finally {
                if (fout != null) {
                    try {
                        fout.close();
                    } catch (IOException e) {
                        java.lang.System.out.println("关闭文件流时发生错误");
                        e.printStackTrace();
                    }
                }
            }

        }
        else{

            System.out.println("模拟磁盘不存在，无需重新创建");

        }
    }

    //G&&S
    public  contextController getContextcontroller() {
        return contextcontroller;
    }

    public void setContextcontroller(contextController contextcontroller) {
        contextcontroller = contextcontroller;
        openOperator.setContextControllers(contextcontroller);
        disk.setContextController(contextcontroller);

    }

    public ChangFileAttrController getChangFileAttrController() {
        return changFileAttrController;
    }

    public void setChangFileAttrController(ChangFileAttrController changFileAttrController) {

        this.changFileAttrController = changFileAttrController;
        openOperator.setChangFileAttrController(changFileAttrController);
    }

    public ChangeDirAttrController getChangeDirAttrController() {
        return changeDirAttrController;
    }

    public void setChangeDirAttrController(ChangeDirAttrController changeDirAttrController) {
        this.changeDirAttrController = changeDirAttrController;
        openOperator.setChangeDirAttrController(changeDirAttrController);
    }

    public EditController getEditController() {
        return editController;
    }

    public void setEditController(EditController editController) {
        this.editController = editController;
        openOperator.setEditController(editController);
    }

}
