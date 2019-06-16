package Comunication;

import Apperance.Chat;

import java.net.*;
import java.io.*;

public class ChatClientThread extends Thread
{
    protected Socket socket;
    protected BufferedReader socketIn;
    protected Chat chatClient;
    public boolean stopped = false;
    public ChatClientThread(Chat chatClient, Socket socket,
                            BufferedReader socketIn)
    {
        this.socket = socket;
        this.socketIn = socketIn;
        this.chatClient = chatClient;
    }
    public void run()
    {
        String line = null;
        while(!stopped){
            try{
                line = socketIn.readLine();
            }
            catch(IOException e){
                break;
            }
            if (line == null){
                break;
            }
            processMessage(line);
        }
        chatClient.clientThreadStopped();
    }
    public void processMessage(String line)
    {
        if (line.length() < 5){
            chatClient.insertText(line + "\n");
            return;
        }
        String command = line.substring(0, 5);
        if (command.equals("/quit")){
            stopped = true;
        }
        else if (command.equals("/nick")){
            if (line.length() < 7){
                chatClient.insertText("Błędna odpowiedź serwera!");
                return;
            }
            String nick = line.substring(6, line.length());
            chatClient.addNick(nick);
        }
        else if(command.equals("/nonk")){
            chatClient.insertText(
                    "Najpierw określ swój nick! (Użyj komendy /nick) \n");
        }
        else if(command.equals("/nkex")){
            chatClient.insertText(
                    "Ten nick jest używany przez innego użytkownika.\n");
        }
        else if(command.equals("/nkok")){
            chatClient.insertText("Nick zaakceptowany!\n");
        }
        else if (command.equals("/nkrm")){
            if (line.length() < 7){
                chatClient.insertText("Błędna odpowiedź serwera!");
                return;
            }
            String nick = line.substring(6, line.length());
            chatClient.removeNick(nick);
        }
        else{
            chatClient.insertText(line + "\n");
        }
    }
    public void interrupt()
    {
        super.interrupt();
        try{
            socket.close();
        }
        catch(IOException e){}
    }
}
