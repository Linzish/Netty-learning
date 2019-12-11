package me.unc.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO客户端
 */
public class NIOClient {

    public static void main(String[] args) throws Exception {

        //创建一个
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞模式
        socketChannel.configureBlocking(false);
        //设置连接参数
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        //连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            //如果连接不成功
            while (!socketChannel.finishConnect()) {
                //即使连接不成功，也不会阻塞在这里，可以甘其他事
                System.out.println("等待连接...");
            }
        }
        //连接成功操作
        //连接成功，发送数据
        String str = "唱跳rap篮球";
        //wrap方法可以直接把直接数组包裹进去，大小就等于直接数组大小
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        //发送数据，将数据写入buffer
        socketChannel.write(buffer);
        //test把代码停在这
        System.in.read();

    }

}
