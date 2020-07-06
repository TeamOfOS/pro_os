package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            //BorderPane root = new BorderPane();
//			BorderPane root = FXMLLoader.load(getClass().getResource("/view/mainView.fxml"));

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/mainView.fxml"));
            BorderPane root = (BorderPane) loader.load();
            Scene scene = new Scene(root);

            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(Main.class.getResource("/view/contextView.fxml"));
            AnchorPane contextView = (AnchorPane) loader1.load();

            root.setCenter(contextView);

            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("操作系统");
            //System.out.println("操作系统啊啊啊");
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
