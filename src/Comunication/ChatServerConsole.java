package Comunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class ChatServerConsole
{
  protected Vector<ChatServerCommThread> threadList;
  protected ChatServerNetThread csNetThread = null;
  protected BufferedReader brInput = null;
  public ChatServerConsole()
  {
    threadList = new Vector<ChatServerCommThread>();
    csNetThread = new ChatServerNetThread(this, 6666);
    csNetThread.start();
    try{
      brInput = new BufferedReader(
              new InputStreamReader(System.in));
    }
    catch(Exception e){
      log("Błąd przy tworzeniu strumienia wejściowego: " + e);
      System.exit(-1);
    }
  }
  public static void main(String args[])
  {
    ChatServerConsole cscon = new ChatServerConsole();
    cscon.start();
  }
  public void start()
  {
    String line;
    while(true){
      try{
        System.out.print(">");
        line = brInput.readLine();
        if (line.equals("quit")){
          shutdown();
        }
        else if (line.equals("status")){
          showStatus();
        }
        else if (line.equals("help")){
          showHelp();
        }
        else{
          System.out.println(
                  "Nieznane polecenie. Wpisz help, aby uzyskać pomoc.");
        }
      }
      catch(IOException e){
        log("Błąd wejścia-wyjścia: " + e);
        System.exit(-1);
      }
      catch(Exception e){
        log("Błąd ogólny: " + e);
        System.exit(-1);
      }
    }
  }
  public void addThread(ChatServerCommThread thread)
  {
    threadList.add(thread);
  }
  public void removeThread(ChatServerCommThread thread)
  {
    threadList.remove(thread);
  }
  public void log(String line)
  {
    //System.out.println(line);
  }
  public void shutdown()
  {
    System.out.println("Kończenie pracy...");
    csNetThread.interrupt();
    for(int i = 0; i < threadList.size(); i++){
      threadList.elementAt(i).interrupt();
    }
    try{
      Thread.sleep(200);
    }
    catch(Exception e){}
    System.exit(0);
  }
  public void showStatus()
  {
    if (threadList.size() < 1){
      System.out.println("Nie ma żadnych połączeń.");
    }
    else{
      for(int i = 0; i < threadList.size(); i++){
        System.out.println(
                threadList.elementAt(i).getInfo());
      }
    }
  }
  public void showHelp(){
    System.out.println(
            "Rozpoznawane polecenia to: status help quit");
  }
}
