package me.unc.netty.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 群聊系统服务端
 * 1.监听客户端连接和发送的消息
 * 2.转发客户端发送的信息到其他客户端
 */
public class GroupChatServer {
    //定义属性

    private Selector selector;   //选择器selector
    private ServerSocketChannel listenChannel; //服务端
    private static final int PORT = 6667;  //端口

    //构造器，初始化工作放在这
    public GroupChatServer() {

        try {
            //创建选择器select
            selector = Selector.open();
            //创建serverSocketChannel
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            listenChannel.configureBlocking(false);
            //注册serverSocketChannel到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //监听
    public void listen() {
        System.out.println("服务器启动...");
        try {
            //循环处理
            while (true) {
                int count = selector.select(2000);
                if (count > 0) { //如果有事件处理
                    //遍历得到的selectionKey集合
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        //去除key
                        SelectionKey key = keys.next();

                        //处理accept
                        if (key.isAcceptable()) {
                            SocketChannel accept = listenChannel.accept();
                            //设置非阻塞模式
                            accept.configureBlocking(false);
                            //将accept的SocketChannel注册到selector中
                            accept.register(selector, SelectionKey.OP_READ);
                            //提示
                            System.out.println(accept.getRemoteAddress() + " 上线 ");
                        }
                        if (key.isReadable()) {//通道可读状态
                            //处理读逻辑
                            readData(key);
                        }
                        //删除当前的key，防止重操作
                        keys.remove();
                    }
                } else {
//                    System.out.println("服务器等待...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    //读取客户端消息
    private void readData(SelectionKey selectionKey) {
        //定义一个SocketChannel
        SocketChannel channel = null;
        try {
            //获取channel
            channel = (SocketChannel) selectionKey.channel();
            //创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //读取到buffer
            int read = channel.read(buffer);
            //处理数据
            if (read > 0) {
                String msg= new String(buffer.array());
                //输出信息
                System.out.println("来自客户端：" + msg);

                //向其他客户端转发消息
                boardCastMsg(msg, channel);
            }
        } catch (IOException e) {
            //离线处理
            try {
                System.out.println(channel.getRemoteAddress() + " 离线 ");
                //取消注册
                selectionKey.cancel();
                //挂壁通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //转发消息到其他客户端，注意要排除自己
    private void boardCastMsg(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息...");
        for (SelectionKey key : selector.keys()) {
            //通过key获取对应SocketChannel
            SelectableChannel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                //将msg写入buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer写入channel
                ((SocketChannel) targetChannel).write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        //创建服务端
        GroupChatServer chatServer = new GroupChatServer();
        //监听客户端
        chatServer.listen();
    }

}
