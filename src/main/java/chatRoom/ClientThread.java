package chatRoom;

import java.io.BufferedReader;
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
  String conment = null;
  while ((conment = br.readLine()) != null) {
    System.out.println(conment);
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
