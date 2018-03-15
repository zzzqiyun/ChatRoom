package NIOChatRoom;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Created on 2018/3/14.
 *
 * @author qiyun
 */
public class NServer{
  //���ڼ������Channel��״̬��selecter
 private Selector selector = null;
 public static final int PORT = 4444;
 public static  Charset charset = Charset.forName("UTF-8");
 public void init()throws IOException{
     selector = Selector.open();
     ServerSocketChannel server = ServerSocketChannel.open();
     InetSocketAddress isa = new InetSocketAddress("127.0.0.1",PORT);
     server.bind(isa);
     server.configureBlocking(false);
     server.register(selector, SelectionKey.OP_ACCEPT);
     while(selector.select()>0){
       //���δ���selector�ϵ�ÿһ����ѡ���selectorKey
       for (SelectionKey sk:selector.selectedKeys()) {
         selector.selectedKeys().remove(sk);
         //���sk��Ӧ��channel�����ͻ��˵���������
         if (sk.isConnectable()) {
           //����accept�����������ӣ�������������SocketChannel
           SocketChannel sc = server.accept();
           sc.configureBlocking(false);
           //��socketChannelҲע�ᵽselector��
           sc.register(selector, SelectionKey.OP_ACCEPT);
           //��socketChannel���ó�׼��������������
           sk.interestOps(SelectionKey.OP_ACCEPT);
         }
         //���socketChannel��Ӧ��channel������Ҫ��ȡ
         if (sk.isAcceptable()) {
           //��ȡ��selectionKey��Ӧ��channel,��Channel���пɶ�ȡ������
           SocketChannel sc = server.accept();
           //����׼��ִ�ж�ȡ���ݵ�bytebuffer
           ByteBuffer buff = ByteBuffer.allocate(1024);
           String content = "";
           try {
             while (sc.read(buff) > 0) {
               buff.flip();
               content += charset.decode(buff);
             }
             System.out.println("��ȡ�����ݣ�" + content);
             //��sk��Ӧ��Channel���ó�׼����һ�ζ�ȡ
             sk.interestOps(SelectionKey.OP_ACCEPT);
           } catch (IOException ex) {
             //������񵽵�sk��Ӧ��channel�������쳣����������channel��Ӧ�Ŀͻ��˳��������⣬���Դ�selector��ȡ��sk���ע��
             sk.channel();
             if(sk.channel()!=null){
               sk.channel().close();
             }
           }
           //���content�ĳ��ȴ���0����������Ϣ��Ϊ��
          if(content.length()>0){
            for (SelectionKey key:selector.keys()) {
              //��ȡ��key��Ӧ��channel
              Channel targetChannel = key.channel();
              //�����channel��SocketChannel����
              //������������д���channel��
              if(targetChannel instanceof  SocketChannel){
                SocketChannel dest =(SocketChannel) key.channel();
                dest.write(charset.encode(content));
              }
            }
          }
         }
       }
       }
     }
 public static void main(String args[])throws  Exception{
   NServer nServer = new NServer();
   nServer.init();
 }

}
