package me.unc.netty.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 群聊系统客户端
 */
public class GroupChatClient {
    //定义属性
    private static final String HOST = "127.0.0.1";  //主机名
    private static final int PORT = 6667;  //端口
    private Selector selector;   //选择器selector
    private SocketChannel socketChannel; //客户端socket
    private String username;

    //构造器，完成初始化工作
    public GroupChatClient() {
        try {
            //创建selector
            selector = Selector.open();
            //创建socketChannel
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            //设置非阻塞模式
            socketChannel.configureBlocking(false);
            //注册selector
            socketChannel.register(selector, SelectionKey.OP_READ);
            //设置username
            username = socketChannel.getLocalAddress().toString().substring(1);
            //提示
            System.out.println(username + " is ok...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //向服务器发个
    public void sendMsg(String msg) {
        msg = username + " : " + msg;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //读取服务端消息
    public void readMsg() {
        try {
            int readChannel = selector.select();
            if (readChannel > 0) {  //有可读通道
                //获取响应的selectedKey
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) { //直接处理可读信息
                        //得到相关的通道
                        SocketChannel sc = (SocketChannel)key.channel();
                        //获取buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //读取buffer
                        sc.read(buffer);
                        //将读取的信息转换为字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                }
                //删除当前的key，防止重操作
                iterator.remove();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //创建客户端
        GroupChatClient chatClient = new GroupChatClient();
        //开启一条线程读取服务端的信息
        new Thread(() -> {
            //每隔3秒读取一次
            while (true) {
                chatClient.readMsg();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //发送数据
        //创建一个扫描器扫描输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) { //读取一行，阻塞
            String m = scanner.nextLine();
            chatClient.sendMsg(m);
        }
    }

}
