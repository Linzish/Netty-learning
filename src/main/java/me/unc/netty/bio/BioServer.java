package me.unc.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BioServer {

    public static void main(String[] args) throws IOException {

        //1.使用线程池
        //2.如果有客户端连接。就创建一条线程处理客户端请求

        //创建线程池
        ExecutorService executorService = Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器启动....");

        while (true) {
            //监听，等待客户端连接
            final Socket accept = serverSocket.accept();
            System.out.println("连接到一个客户端...");

            //创建一条线程，与之通信
            executorService.execute(() -> {
                //和客户端通信/处理客户端请求
                try {
                    clientHandler(accept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private static void clientHandler(Socket accept) throws IOException {
        try {
            byte[] bytes = new byte[1024];
            //获取输入流
            InputStream inputStream = accept.getInputStream();
            //处理逻辑
            //循环的读取客户端发送的数据
            while (true) {
                int read = inputStream.read(bytes);
                if (read != 1) {
                    System.out.println("来自客户端的信息");
                    //测试，输出线程id
                    //输出客户端信息
                    System.out.println("线程信息：id = " + Thread.currentThread().getId() +
                            " name = " + Thread.currentThread().getName() +
                            " 信息：" + new String(bytes, 0, read) );
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭和client的连接");
            accept.close();
        }
    }

}
