package me.unc.netty.inout;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.TimeUnit;

public class MyServer {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //.handler(new LoggingHandler(LogLevel.INFO)) //netty提供的log
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //入站的处理handler，进行解码
                            pipeline.addLast(new MyByteToLongDecoder());
                            //出站handler，进行编码\
                            //注意出站和入站handler不冲突
                            pipeline.addLast(new MyLongToByteEncoder());
                            //自定义handler
                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            //启动
            ChannelFuture channelFuture = server.bind(7777).sync();
            channelFuture.addListener((ChannelFutureListener) cf -> {
                if (channelFuture.isSuccess()) {
                    System.out.println("服务器启动");
                    System.out.println("当前监听接口 7777");
                } else {
                    System.out.println("服务器启动失败");
                }
            });
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
