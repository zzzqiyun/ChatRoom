package chatRoom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created on 2018/3/12.
 *
 * @author qiyun
 */
public class ServerThread extends Thread{
  private Socket socket;
  PrintStream ps = null;
  BufferedReader br = null;
  public ServerThread(Socket socket){
    this.socket = socket;
  }
  public void run(){
    try{
     br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
      ps =new PrintStream(socket.getOutputStream());
      String line = null;
      while((line=br.readLine())!=null){
        //如果读到的行以USER_ROUND开始，并以其结束
        //则可以判断读到的是用户登录的信息
        if(line.startsWith(LogoProtocol.USER_ROUND)&&line.endsWith(LogoProtocol.USER_ROUND)){
          //得到真实消息
          String username = getReanMsg(line);
          //如果用户名重复
          if(Server.clients.map.containsKey(username)){
            System.out.println("用户名重复！");
            ps.println(LogoProtocol.NAME_REP);
          }else{
            System.out.println("成功");
            ps.println(LogoProtocol.LOGIN_SUCCESS);
            Server.clients.put(username,ps);
          }
        }
        //如果读到的行是以PRICATE_ROUND开始，并以其结束
        //则可以确定是私聊信息，私聊信息指向特定的输出流发送
        else if(line.startsWith(LogoProtocol.PRIVATE_ROUND)&&line.endsWith(LogoProtocol.PRIVATE_ROUND)){
          //得到真实信息
          String userAndMsg = getReanMsg(line);
          String user = userAndMsg.split(LogoProtocol.SPLIT_SIGN)[0];
          String msg = userAndMsg.split(LogoProtocol.SPLIT_SIGN)[1];
          //获取私聊用户的输出流并发送私聊信息
          Server.clients.map.get(user).println(Server.clients.getKeyByVaule(ps)+"悄悄对你说："+msg);
        }
        //公聊要向每个Socket发送
        else{
          String msg = getReanMsg(line);
          for (PrintStream clientsPs:Server.clients.valueSet()) {
            clientsPs.println(Server.clients.getKeyByVaule(ps)+"说："+msg);
          }
        }
      }
    }catch (Exception e){
      //捕获异常后，表明该socket对应的客户端已经出现了问题
      //所以程序将其对应的输出流从map中移除
      Server.clients.removeByValue(ps);
      System.out.println("共有多少聊天用户："+Server.clients.map.size());
      try{
        if(br!=null){
           br.close();
        }
        if(ps!=null){
          ps.close();
        }
        if(socket!=null){
          socket.close();
        }
      }catch (Exception ee){
        ee.printStackTrace();
      }
    }

  }


  public String getReanMsg(String line){
    return line.substring(LogoProtocol.PPROTOCOL_LEN,line.length()-LogoProtocol.PPROTOCOL_LEN);
  }
}
