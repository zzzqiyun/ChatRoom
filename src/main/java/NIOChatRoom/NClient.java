package NIOChatRoom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created on 2018/3/15.
 *
 * @author qiyun
 */
public class NClient {
  private int PORT = 4444;
  Selector selector = null;
  SocketChannel sc = null;
  private Charset charset = Charset.forName("UTF-8");
  public void init()throws IOException{
    selector = Selector.open();
    InetSocketAddress isa = new InetSocketAddress("127.0.0.1",PORT);
    sc = SocketChannel.open(isa);
    sc.configureBlocking(false);
    sc.register(selector, SelectionKey.OP_READ);
    //启动读取服务器的线程
   new ClientThread().start();
   //创建键盘输入流
    Scanner scan = new Scanner(System.in);
    while(scan.hasNextLine()){
      String line = scan.nextLine();
      sc.write(charset.encode(line));
    }
  }

  private class ClientThread extends Thread{
    public void run(){
      try{
        while(selector.select()>0){
          for(SelectionKey key :selector.keys()){
            selector.selectedKeys().remove(key);
            if(key.isAcceptable()){
              //使用NIO读取channel的数据
              SocketChannel sc = (SocketChannel) key.channel();
              String content = null;
              ByteBuffer buff = ByteBuffer.allocate(1024);
              while(sc.read(buff)>0){
                sc.read(buff);
                buff.flip();
                content += charset.decode(buff);
                System.out.println("聊天信息："+content);
                //为下一次读取做准备
                key.interestOps();
              }
            }
          }
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    }
  }
  public static void main(String args[])throws IOException{
    new NClient().init();
  }
}
