package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import os.OS;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/*
该类是在更改文件属性时打开界面的控制类
 */


public class ChangFileAttrController implements Initializable {
    @FXML
    private TextField textOfFileName;//文件名称
    @FXML
    private ChoiceBox choiceOfReadAttr ;//读写属性
    @FXML
    private ChoiceBox choiceOfAttr;//文件的属性
    @FXML
    private Button saveBtn;//保存按钮
    @FXML
    private Button cancelBtn;//取消按钮

    private OS os;
    private Stage secondStage = new Stage();
    private String secFileName = null;
    private boolean isOnlyRead = true;
    private boolean isTxt = true;
    private Alert alert ;

    //构造方法
    public ChangFileAttrController() throws Exception{
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("错误警告");
    }

    //取消按钮操作
    public void cancelAction(){
            secondStage.hide();
    }

    //保存按钮操作
    public void saveAction() throws IOException {
        secFileName = textOfFileName.getText();
        int i = choiceOfReadAttr.getSelectionModel().getSelectedIndex();
        if(i==0){
            //只读文本
            isOnlyRead = true;
        }
        if(i==1){
            //可读可写
            isOnlyRead = false;
        }
        int t = choiceOfAttr.getSelectionModel().getSelectedIndex();
        if (t==0){
            //普通文版
            isTxt = true;
        }
        if (t==1){
            isTxt = false;
        }
        int numRe = os.openOperator.saveFileAttr();
        if ( numRe==0){
            alert.setContentText("在同一文件夹中出现命名重复，更改属性失败");
            alert.show();
        }
        else if ( numRe==1){
            alert.setContentText("命名过长不可使用");
            alert.show();
        }
        else if ( numRe==2){
            alert.setContentText("未找到相应文件");
            alert.show();
        }
        else {
            secondStage.hide();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }


    //G&&S
    public TextField getTextOfFileName() {
        return textOfFileName;
    }

    public void setTextOfFileName(TextField textOfFileName) {
        this.textOfFileName = textOfFileName;
    }

    public ChoiceBox getChoiceOfReadAttr() {
        return choiceOfReadAttr;
    }

    public void setChoiceOfReadAttr(ChoiceBox choiceOfReadAttr) {
        this.choiceOfReadAttr = choiceOfReadAttr;
    }

    public ChoiceBox getChoiceOfAttr() {
        return choiceOfAttr;
    }

    public void setChoiceOfAttr(ChoiceBox choiceOfAttr) {
        this.choiceOfAttr = choiceOfAttr;
    }

    public Button getSaveBtn() {
        return saveBtn;
    }

    public void setSaveBtn(Button saveBtn) {
        this.saveBtn = saveBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }

    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    public Stage getSecondStage() {
        return secondStage;
    }

    public void setSecondStage(Stage secondStage) {
        this.secondStage = secondStage;
    }

    public String getSecFileName() {
        return secFileName;
    }

    public void setSecFileName(String secFileName) {
        this.secFileName = secFileName;
    }

    public boolean isOnlyRead() {
        return isOnlyRead;
    }

    public void setOnlyRead(boolean onlyRead) {
        isOnlyRead = onlyRead;
    }

    public boolean isTxt() {
        return isTxt;
    }

    public void setTxt(boolean txt) {
        isTxt = txt;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }
}
