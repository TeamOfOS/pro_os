package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import os.OS;

/*
该类是在更改目录属性时打开界面的控制类
 */

public class ChangeDirAttrController {

    @FXML
    private Button saveBtn1 ;//保存按钮
    @FXML
    private Button cancelBtn1;//取消按钮
    @FXML
    private TextField textOfDir;//更改文件名

    private Alert alert;//警告
    private OS os;
    private Stage dirAttrStage = new Stage();

    //构造方法
    public ChangeDirAttrController(){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("错误警告");
    }

    //取消按钮操作
    public void cancelAction1(){
        dirAttrStage.hide();


    }

    //保存按钮操作
    public void saveAction1(){

        if(!os.openOperator.saveDirAttr()){
            alert.setContentText("在同一文件夹中，命名重复");
            alert.show();
        }
        else {
            dirAttrStage.hide();
        }

    }

    //G&&S
    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    public Stage getDirAttrStage() {
        return dirAttrStage;
    }

    public void setDirAttrStage(Stage dirAttrStage) {
        this.dirAttrStage = dirAttrStage;
    }

    public Button getSaveBtn1() {
        return saveBtn1;
    }

    public void setSaveBtn1(Button saveBtn1) {
        this.saveBtn1 = saveBtn1;
    }

    public Button getCancelBtn1() {
        return cancelBtn1;
    }

    public void setCancelBtn1(Button cancelBtn1) {
        this.cancelBtn1 = cancelBtn1;
    }

    public TextField getTextOfDir() {
        return textOfDir;
    }

    public void setTextOfDir(TextField textOfDir) {
        this.textOfDir = textOfDir;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }
}
