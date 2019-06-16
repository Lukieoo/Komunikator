package Apperance;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller {
    public AnchorPane root;
    public JFXButton connect;
    public static String NICK;

    public void exit(MouseEvent mouseEvent) {

        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setOnFinished(event -> System.exit(0));

        ft.play();

    }

    public void Connect(ActionEvent event) throws Exception {

        Stage stage = (Stage) connect.getScene().getWindow();
        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setOnFinished(event1 ->  stage.close());
        ft.play();

        Chatmain chatmain=new Chatmain();
        chatmain.start();
    }
}
