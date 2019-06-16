package Apperance;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Chatmain {

    public AnchorPane root;
    public Stage stage=new Stage();
    public void start() throws Exception {

        root=new AnchorPane();

        root= FXMLLoader.load(getClass().getResource("Chat.fxml"));

        stage.setScene(new Scene(root, 800, 500));
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();

        Image icon = new Image("Assets/icon.png");
        stage.getIcons().add(icon);
    }
}
