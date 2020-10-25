package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import os.OS;

import java.io.IOException;

/*
该类是编辑文件界面的控制器类
 */

public class EditController {

    @FXML
    private Button saveBtn2;//保存按钮
    @FXML
    private Button cancelBtn2;//取消按钮
    @FXML
    private TextArea contextArea;//内容展示区
    @FXML
    private Text textOfInfo;//文件信息
    @FXML
    private ScrollPane scrollPane;//滚动
    @FXML
    private Text copyStr;//复制文件的来源
    private OS os;
    private Stage editStage = new Stage();
    private Alert alert;
    //构造方法
    public EditController(){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("错误警告");
    }

    //保存按钮操作
    public void saveAction2() throws IOException {
       if (!os.openOperator.saveEditContext()){
           alert.setContentText("写入磁盘失败，无法保存");
           alert.show();
       }
       else {
           editStage.hide();
       }
    }

    //取消按钮操作
    public void cancelAction2(){
        editStage.hide();
    }

    //G&&S
    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    public Stage getEditStage() {
        return editStage;
    }

    public void setEditStage(Stage editStage) {
        this.editStage = editStage;
    }

    public Button getSaveBtn2() {
        return saveBtn2;
    }

    public void setSaveBtn2(Button saveBtn2) {
        this.saveBtn2 = saveBtn2;
    }

    public Button getCancelBtn2() {
        return cancelBtn2;
    }

    public void setCancelBtn2(Button cancelBtn2) {
        this.cancelBtn2 = cancelBtn2;
    }

    public TextArea getContextArea() {
        return contextArea;
    }

    public void setContextArea(TextArea contextArea) {
        this.contextArea = contextArea;
    }

    public Text getTextOfInfo() {
        return textOfInfo;
    }

    public void setTextOfInfo(Text textOfInfo) {
        this.textOfInfo = textOfInfo;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Text getCopyStr() {
        return copyStr;
    }

    public void setCopyStr(Text copyStr) {
        this.copyStr = copyStr;
    }



}
