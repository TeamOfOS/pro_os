package application;

import controller.ChangFileAttrController;
import controller.ChangeDirAttrController;
import controller.EditController;
import controller.contextController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import os.OS;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            //主界面
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/mainView.fxml"));
            BorderPane root = (BorderPane) loader.load();
            Scene scene = new Scene(root);

            //内容界面
            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(Main.class.getResource("/view/contextView.fxml"));
            AnchorPane contextView = (AnchorPane) loader1.load();
            contextController contextController = loader1.getController();
            OS os = OS.getInstance();
            os.setContextcontroller(contextController);
            contextController.setOs(os);
            root.setCenter(contextView);

            //更改文件属性界面
            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(Main.class.getResource("/view/changFileAttrView.fxml"));
            AnchorPane changFileAttrView = (AnchorPane) loader2.load();
            ChangFileAttrController changFileAttrController = loader2.getController();
            os.setChangFileAttrController(changFileAttrController);
            changFileAttrController.setOs(os);
            Scene scene1 = new Scene(changFileAttrView);
            os.changFileAttrController.getSecondStage().setScene(scene1);
            os.changFileAttrController.getSecondStage().setTitle("更改文件属性");

            //更改目录属性界面
            FXMLLoader loader3 = new FXMLLoader();
            loader3.setLocation(Main.class.getResource("/view/changeDirAttrView.fxml"));
            Pane changeDirAttrView =  loader3.load();
            ChangeDirAttrController changeDirAttrController = loader3.getController();
            os.setChangeDirAttrController(changeDirAttrController);
            changeDirAttrController.setOs(os);
            Scene scene2 = new Scene(changeDirAttrView);
            os.changeDirAttrController.getDirAttrStage().setScene(scene2);
            os.changeDirAttrController.getDirAttrStage().setTitle("更改目录属性");

            //编辑文件内容界面
            FXMLLoader loader4 = new FXMLLoader();
            loader4.setLocation(Main.class.getResource("/view/editView.fxml"));
            AnchorPane editView = (AnchorPane) loader4.load();
            EditController editController = loader4.getController();
            os.setEditController(editController);
            editController.setOs(os);
            Scene scene3 = new Scene(editView);
            os.editController.getEditStage().setScene(scene3);
            os.editController.getEditStage().setTitle("编辑文件");

            //主界面的界面设置
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("操作系统");
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
