package chatRoom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created on 2018/3/12.
 *
 * @author qiyun
 */
public class ClientThread extends Thread{
  private Socket socket;
  BufferedReader br = null;
  public ClientThread(BufferedReader br){
    this.br = br;
  }

  public void run(){
try {
  String s = null;
  while ((s = br.readLine()) != null) {
    System.out.println(s);
  }
}catch (Exception e){
  e.printStackTrace();
}
finally {
  try{
    if(br!=null){
      br.close();
    }
  }catch (Exception ee){
    ee.printStackTrace();
  }
}
  }
}
