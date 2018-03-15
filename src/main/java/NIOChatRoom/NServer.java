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
  //用于检测所有Channel的状态的selecter
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
       //依次处理selector上的每一个已选择的selectorKey
       for (SelectionKey sk:selector.selectedKeys()) {
         selector.selectedKeys().remove(sk);
         //如果sk对应的channel包含客户端的连接请求
         if (sk.isConnectable()) {
           //调用accept方法接受连接，产生服务器的SocketChannel
           SocketChannel sc = server.accept();
           sc.configureBlocking(false);
           //将socketChannel也注册到selector里
           sc.register(selector, SelectionKey.OP_ACCEPT);
           //将socketChannel设置成准备接受其他请求
           sk.interestOps(SelectionKey.OP_ACCEPT);
         }
         //如果socketChannel对应的channel有数据要读取
         if (sk.isAcceptable()) {
           //获取该selectionKey对应得channel,该Channel中有可读取的数据
           SocketChannel sc = server.accept();
           //定义准备执行读取数据的bytebuffer
           ByteBuffer buff = ByteBuffer.allocate(1024);
           String content = "";
           try {
             while (sc.read(buff) > 0) {
               buff.flip();
               content += charset.decode(buff);
             }
             System.out.println("读取的数据：" + content);
             //将sk对应的Channel设置成准备下一次读取
             sk.interestOps(SelectionKey.OP_ACCEPT);
           } catch (IOException ex) {
             //如果捕获到的sk对应得channel出现了异常，即表明该channel对应的客户端出现了问题，所以从selector中取消sk大的注册
             sk.channel();
             if(sk.channel()!=null){
               sk.channel().close();
             }
           }
           //如果content的长度大于0，即聊天信息不为空
          if(content.length()>0){
            for (SelectionKey key:selector.keys()) {
              //获取该key对应的channel
              Channel targetChannel = key.channel();
              //如果该channel是SocketChannel对象
              //将读到的内容写入该channel中
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
