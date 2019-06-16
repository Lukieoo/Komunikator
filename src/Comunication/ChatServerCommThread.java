package Comunication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatServerCommThread extends Thread
{
  protected Socket socket;
  protected ChatServerConsole cscon;
  protected BufferedReader brinp = null;
  protected DataOutputStream out = null;
  public String nick = null;
  public String threadName;
  public boolean stopped = false;
  public ChatServerCommThread(ChatServerConsole cscon,
                              Socket socket)
  {
    this.socket = socket;
    this.cscon = cscon;
    threadName = getName();
  }
  public void run()
  {
    //inicjalizacja strumieni
    try{
      brinp = new BufferedReader(
              new InputStreamReader(
                      socket.getInputStream()
              )
      );
      out = new DataOutputStream(socket.getOutputStream());
    }
    catch(IOException e){
      cscon.log(threadName + "| Błąd przy tworzeniu strumieni " + e);
      cscon.removeThread(this);
      return;
    }
    String line = null;

    //pętla główna
    while(!stopped){
      try{
        line = brinp.readLine();
        cscon.log(threadName + "| Odczytano linię: " + line);

        //osiągnięty koniec strumienia (brak połączenia)
        if(line == null){
          cscon.log(threadName +
                  "| Zakończenie pracy z klientem: " + socket);
          break;
        }
        else{
          processMessage(line);
        }
      }
      catch(IOException e){
        cscon.log(threadName + "| Błąd wejścia-wyjścia: " + e);
        break;
      }
    }

    //kończenie pracy wątku
    try{
      if(!socket.isClosed()) socket.close();
    }
    catch(IOException e){}
    stopped = true;
    cscon.removeThread(this);
    sendToAll("/nkrm " + this.nick);
    cscon.log(threadName + "| Wątek zatrzymany.");
  }
  public void processMessage(String line)
  {
    cscon.log(threadName + "| Przetwarzam linię: " + line);
    if (line.length() < 5){
      if (nick != null){
        sendToAll(nick + "> " + line);
      }
      else{
        send("/nonk");
      }
      return;
    }
    String command = line.substring(0, 5);
    if (command.equals("/quit")){
      send("/quit");
      stopped = true;
    }
    else if (command.equals("/nick")){
      if (line.length() < 7){
        send("/nonk");
        return;
      }
      String nick = line.substring(6, line.length());
      if (!nickExists(nick)){
        send("/nkok");
        if (this.nick != null){
          sendToAll("/nkrm " + this.nick);
        }
        else{
          sendAllNicks();
        }
        this.nick = nick;
        sendToAll("/nick " + nick);
      }
      else{
        send("/nkex");
      }
    }
    else{
      if (nick != null){
        sendToAll(nick + "> " + line);
      }
      else{
        send("/nonk");
      }
    }
  }
  public boolean nickExists(String nick)
  {
    synchronized(cscon.threadList){
      for (int i = 0; i < cscon.threadList.size(); i++){
        ChatServerCommThread st = cscon.threadList.elementAt(i);
        if((st.nick != null) && st.nick.equals(nick)){
          return true;
        }
      }
      return false;
    }
  }

  public void sendAllNicks()
  {
    synchronized(cscon.threadList){
      for (int i = 0; i < cscon.threadList.size(); i++){
        String nick = cscon.threadList.elementAt(i).nick;
        if ((nick != null) && !nick.equals(this.nick)){
          send("/nick " + nick);
        }
      }
    }
  }

  public void send(String line)
  {
    try{
      out.writeBytes(line + "\n");
      cscon.log(threadName + "| Wysłano: " + line);
    }
    catch(IOException e){
      cscon.log(threadName + "| Błąd wejścia-wyjścia: " + e);
    }
  }
  public void sendToAll(String line){
    synchronized(cscon.threadList){
      for (int i = 0; i < cscon.threadList.size(); i++){
        if (cscon.threadList.elementAt(i).nick != null)
          cscon.threadList.elementAt(i).send(line);
      }
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
  public String getInfo()
  {
    String info = this.getName() + "| ";
    info += "IP " + socket.getInetAddress().getHostAddress() + " ";
    info += "Port " + socket.getPort() + " ";
    info += "nick:" + ((nick != null)?nick:"-");
    return info;
  }
}
