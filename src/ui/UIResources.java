package ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;

public class UIResources {
    public static Node getDirectoryIcon(){
        return    new ImageView(new Image(UIResources.class.getResourceAsStream("/ui/directory.png")));
    }
    public static Node getexeFileIcon(){
        return    new ImageView(new Image(UIResources.class.getResourceAsStream("/ui/exefile.png")));
    }
    public static Node gettxtFileIcon(){
        return    new ImageView(new Image(UIResources.class.getResourceAsStream("/ui/txtfile.png")));
    }

}
