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
        //�������������USER_ROUND��ʼ�����������
        //������ж϶��������û���¼����Ϣ
        if(line.startsWith(LogoProtocol.USER_ROUND)&&line.endsWith(LogoProtocol.USER_ROUND)){
          //�õ���ʵ��Ϣ
          String username = getReanMsg(line);
          //����û����ظ�
          if(Server.clients.map.containsKey(username)){
            System.out.println("�û����ظ���");
            ps.println(LogoProtocol.NAME_REP);
          }else{
            System.out.println("�ɹ�");
            ps.println(LogoProtocol.LOGIN_SUCCESS);
            Server.clients.put(username,ps);
          }
        }
        //���������������PRICATE_ROUND��ʼ�����������
        //�����ȷ����˽����Ϣ��˽����Ϣָ���ض������������
        else if(line.startsWith(LogoProtocol.PRIVATE_ROUND)&&line.endsWith(LogoProtocol.PRIVATE_ROUND)){
          //�õ���ʵ��Ϣ
          String userAndMsg = getReanMsg(line);
          String user = userAndMsg.split(LogoProtocol.SPLIT_SIGN)[0];
          String msg = userAndMsg.split(LogoProtocol.SPLIT_SIGN)[1];
          //��ȡ˽���û��������������˽����Ϣ
          Server.clients.map.get(user).println(Server.clients.getKeyByVaule(ps)+"���Ķ���˵��"+msg);
        }
        //����Ҫ��ÿ��Socket����
        else{
          String msg = getReanMsg(line);
          for (PrintStream clientsPs:Server.clients.valueSet()) {
            clientsPs.println(Server.clients.getKeyByVaule(ps)+"˵��"+msg);
          }
        }
      }
    }catch (Exception e){
      //�����쳣�󣬱�����socket��Ӧ�Ŀͻ����Ѿ�����������
      //���Գ������Ӧ���������map���Ƴ�
      Server.clients.removeByValue(ps);
      System.out.println("���ж��������û���"+Server.clients.map.size());
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
