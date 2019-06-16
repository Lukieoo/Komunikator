package Apperance;

import Comunication.ChatClientThread;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Chat implements Initializable{

    public AnchorPane root;
    public ImageView logoutbutton;
    public JFXTextField txfield;
    public JFXTextArea conversation;
    public Button coon;


    public JFXListView<String> listview = new JFXListView<>();
    public ObservableList<String> item=FXCollections.observableArrayList();

    protected boolean connected;
    protected Socket socket;
    protected BufferedReader socketIn;
    protected DataOutputStream socketOut;
    protected ChatClientThread clientThread;

    public void exit(MouseEvent mouseEvent) {

        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setOnFinished(event -> System.exit(0));

        ft.play();
    }

    public void logout(MouseEvent mouseEvent) throws Exception {
        Stage stage = (Stage) logoutbutton.getScene().getWindow();
        FadeTransition ft = new FadeTransition(Duration.millis(700), root);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setOnFinished(event1 ->  stage.close());
        ft.play();

        Main main=new Main();
        Stage newStage=new Stage();
        main.start(newStage);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listview.setItems(item);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Chat();
            }
        });
        txfield.setOnKeyPressed(event -> {
            if (event.getCode()== KeyCode.ENTER){
               // conversation.appendText(txfield.getText()+"\n");


                if (!connected){
                    insertText("Najpierw nawiąż połączenie!\n");
                    return;
                }

                String line = txfield.getText();
                txfield.clear();

                try{
                    socketOut.writeBytes(line + "\n");
                    socketOut.flush();
                }
                catch(IOException e){
                    insertText("Błąd przy wysyłaniu danych: " + e + "\n");
                }
            }

        });
    }

    public void coon(javafx.event.ActionEvent event) {
        connect();
    }

//    public void connect(String host, int port)
    public void connect()
    {
        String host="192.168.1.103";
        int port=6666;
        connected = false;
        insertText("Łączenie z hostem " + host + "\n");
        try{
            socket = new Socket(host, port);
        }
        catch(IOException e){
            insertText("Błąd gniazda: " + e + "\n");
            return;
        }
        insertText("Zakończona inicjalizacja gniazda...\n");
        try{
            socketOut = new DataOutputStream(socket.getOutputStream());
            socketIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
        }
        catch(IOException e){
            insertText("Błąd przy tworzeniu strumieni: " + e + "\n");
            return;
        }
        clientThread = new ChatClientThread(this, socket, socketIn);
        clientThread.start();
        insertText("Połączono z serwerem.\n");
        connected = true;
    }

    public void clientThreadStopped()
    {
        removeAllstNicks();
        insertText("Rozłączono!\n");
        connected = false;
        try{
            if(!socket.isClosed())
                socket.close();
        }
        catch(IOException e){}
    }
    public void insertText(final String line)
    {
        if(SwingUtilities.isEventDispatchThread()){
            conversation.appendText(line);
        }
        else{
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            conversation.appendText(line);
                        }
                    }
            );
        }
    }
    public void addNick(String nick)
    {
        item.add(nick);
        listview.setItems(item);
    }
    public void removeNick(String nick)
    {
       item.remove(nick);

        listview.setItems(item);
    }
    public void removeAllstNicks()
    {
        item.clear();
        listview.setItems(item);
    }


}
