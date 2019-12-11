package me.unc.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务端
 */
public class NIOServer {

    public static void main(String[] args) throws Exception {

        //创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //创建一个Selector对象
        Selector selector = Selector.open();

        //绑定端口到服务器端监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //把serverSocketChannel 注册到Selector，同时设置关心事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true) {
            //等待2秒
            if (selector.select(2000) == 0) {
                System.out.println("服务器等待中...");
                continue;
            }
            //如果返回的值大于0，表示获取到相关的关心事件，获取相关的selectKey集合
            //selector.selectedKeys()返回关注事件的集合
            //通过selectionKeys方向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //获取selectionKeys迭代器
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                //获取相关的selectionKey
                SelectionKey key = keyIterator.next();
                //根据key对应的事件进行相应的处理
                if (key.isAcceptable()) {
                    //为该客户端生成一个socketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("连接成功  " + socketChannel.hashCode());
                    //将socketChannel设置为非阻塞模式
                    socketChannel.configureBlocking(false);
                    //将socketChannel注册到selector，关注事件为OP_READ，
                    //同时为socketChannel关联一个buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }
                if (key.isReadable()) { //可读取事件
                    //通过key反向获取对应的channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    //获取channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    channel.read(buffer);
                    System.out.println("来自客户端的消息：" + new String(buffer.array()));
                }
                //！！重要操作，处理完逻辑要手动删除key，防止重复操作
                keyIterator.remove();
            }

        }


    }

}
