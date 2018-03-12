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
      //����ѭ�����ϵĵ����Ի���Ҫ�������û���
      while(true){
       String userName = JOptionPane.showInputDialog(tip+"�����û���");
       //���û���ǰ������Э���ַ�������
        ps.println(LogoProtocol.USER_ROUND + userName + LogoProtocol.USER_ROUND);
        //��ȡ�������˵���Ӧ
        String result = readerServer.readLine();
        //����û����ظ�����ʼ��һ��ѭ��
        if(result.endsWith(LogoProtocol.NAME_REP)){
          tip = "�û����ظ������������룡";
          continue;
        }
        if(result.equals(LogoProtocol.LOGIN_SUCCESS)){
          break;
        }
      }
    }
    //�����쳣���ر�������Դ�����˳�����
    catch (UnknownHostException e){
      System.out.println("�Ҳ�������������ȷ���������Ѿ�����");
      closeRs();
      System.exit(1);
    }catch (IOException ex){
      System.out.println("�����쳣�������µ�½��");
      closeRs();
      System.exit(1);
    }
    //�Ը�socket���ڵ�����������ClientThread�߳�
    new ClientThread(readerServer).start();
  }

public void readAndSend(){
    try{
    String line =null;
    while((line=keyIn.readLine())!=null) {
      //�����ȡ����Ϣ����ð�ţ�������//��ͷ������Ϊ���뷢��˽����Ϣ
      if(line.indexOf(":")> 0 &&line.startsWith("//")){
        line = line.substring(2);
        ps.println(LogoProtocol.PRIVATE_ROUND+line.split(":")+LogoProtocol.SPLIT_SIGN+line.split(":")+LogoProtocol.PRIVATE_ROUND);
      }else{
        ps.println(LogoProtocol.MSG_ROUND+line+LogoProtocol.MSG_ROUND);
      }
    }
    }catch (IOException e){
      System.out.println("�����쳣�������µ�½��");
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
