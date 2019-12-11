package me.unc.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty 简易HTTP 服务器
 */
public class NettyHttpServer {

    private static final int PORT = 6668;

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //创建服务端启动对象，并配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //使用链式编程进行设置
            serverBootstrap.group(bossGroup, workGroup)  //设置主从两个线程组
                    .channel(NioServerSocketChannel.class)  //使用NioServerSocketChannel 作为服务端的通道实现（反射）
                    .childHandler(new NettyHttpServerInitializer()); //创建一个通道初始化对象

            //给服务端绑定端口，
            // 并且同步处理，会生成一个ChannelFuture对象（future模式）
            ChannelFuture cf = serverBootstrap.bind(PORT).sync();
            //可以对future模式添加监听器，监听运行结果或关心事件
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (cf.isSuccess()) {
                    System.out.println("当前监听接口 " + PORT);
//                    System.out.println("服务器地址：" + cf.channel().localAddress());
                } else {
                    System.out.println("服务器启动失败");
                }
            });
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

}
