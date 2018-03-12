package chatRoom;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created on 2018/3/12.
 *
 * @author qiyun
 */
public class Server {
   public static final int SERVET_PORT = 5555;
  public static ChatMap<String,PrintStream> clients = new ChatMap<>();
  public void init(){
    try(
    ServerSocket ss = new ServerSocket(SERVET_PORT);
    ) {
      while (true) {
        Socket socket = ss.accept();
        new ServerThread(socket).start();
      }
    }catch (Exception e){
      System.out.println("����������ʧ�ܣ��Ƿ�˿�"+SERVET_PORT+"��ռ��");
    }
  }
  public static void main(String args[]){
    Server server = new Server();
    server.init();
  }
}
