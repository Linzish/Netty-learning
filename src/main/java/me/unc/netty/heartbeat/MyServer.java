package me.unc.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

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
                            //加入一个netty的空闲状态handler
                            /*
                            1.IdleStateHandler是netty处理空闲状态的处理器
                            2.long readerIdleTime：表示多长时间没有读，会发送一个心跳检测包检测是否仍然连接
                            3.long writerIdleTime：表示多少时间没有写，会发送一个心跳检测包检测是否仍然连接
                            4.long allIdleTime：表示多少时间没有读写，会发送一个心跳检测包检测是否仍然连接
                            心跳检测会触发下一个handler的userEventTriggered方法
                             */
                            pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
                            //加入对空闲检测进一步处理的自定义handler
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
