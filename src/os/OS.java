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

    private OS() throws Exception {
    }

    public static synchronized  OS getInstance( ) throws Exception {
        return os;
    }


    //G&&S
    public  contextController getContextcontroller() {
        return contextcontroller;
    }

    public void setContextcontroller(contextController contextcontroller) {
        contextcontroller = contextcontroller;
        openOperator.setContextControllers(contextcontroller);
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
