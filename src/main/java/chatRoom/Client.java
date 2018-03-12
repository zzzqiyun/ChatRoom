package chatRoom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.*;

/**
 * Created on 2018/3/12.
 *
 * @author qiyun
 */
public class Client {
  private static final int PORT= 5555;
 private BufferedReader readerServer = null;
  private  BufferedReader keyIn = null;
  private PrintStream ps = null;
  private  Socket socket =null;
  public void init(){
    try{
      keyIn = new BufferedReader(new InputStreamReader(System.in));
      socket = new Socket("127.0.0.1",PORT);
      ps = new PrintStream(socket.getOutputStream());
      readerServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String tip = " ";
      //采用循环不断的弹出对话框要求输入用户名
      while(true){
       String userName = JOptionPane.showInputDialog(tip+"输入用户名");
       //在用户名前后增加协议字符串后发送
        ps.println(LogoProtocol.USER_ROUND + userName + LogoProtocol.USER_ROUND);
        //读取服务器端的相应
        String result = readerServer.readLine();
        //如果用户名重复，则开始下一次循环
        if(result.endsWith(LogoProtocol.NAME_REP)){
          tip = "用户名重复，请重新输入！";
          continue;
        }
        if(result.equals(LogoProtocol.LOGIN_SUCCESS)){
          break;
        }
      }
    }
    //捕获异常，关闭网络资源，并退出程序
    catch (UnknownHostException e){
      System.out.println("找不到服务器，请确定服务器已经启动");
      closeRs();
      System.exit(1);
    }catch (IOException ex){
      System.out.println("网络异常，请重新登陆！");
      closeRs();
      System.exit(1);
    }
    //以该socket对于的输入流启动ClientThread线程
    new ClientThread(readerServer).start();
  }

public void readAndSend(){
    try{
    String line =null;
    while((line=keyIn.readLine())!=null) {
      //如果读取的信息中有冒号，并且以//开头，则认为是想发送私聊消息
      if(line.indexOf(":")> 0 &&line.startsWith("//")){
        line = line.substring(2);
        ps.println(LogoProtocol.PRIVATE_ROUND+line.split(":")+LogoProtocol.SPLIT_SIGN+line.split(":")+LogoProtocol.PRIVATE_ROUND);
      }else{
        ps.println(LogoProtocol.MSG_ROUND+line+LogoProtocol.MSG_ROUND);
      }
    }
    }catch (IOException e){
      System.out.println("网络异常，请重新登陆！");
      closeRs();
      System.exit(1);
    }
}

  public void closeRs(){
    try {
      if (keyIn != null) {
        keyIn.close();
      }
      if(ps!=null){
        ps.close();
      }
      if(readerServer!=null){
        readerServer.close();
      }
      if(socket!=null){
        socket.close();
      }
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void main(String args[])throws Exception{
 Client client = new Client();
 client.init();
 client.readAndSend();
  }
}
