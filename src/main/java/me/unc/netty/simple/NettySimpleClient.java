package me.unc.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 简易netty客户端
 */
public class NettySimpleClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6668;

    public static void main(String[] args) throws InterruptedException {
        //客户端需要一个事件循环组
        EventLoopGroup eventGroup = new NioEventLoopGroup();
        //创建客户端启动对象
        //注意服务端是ServerBootstrap， 客户端是Bootstrap
        Bootstrap clientBootstrap = new Bootstrap();
        try {
            //链式设置
            clientBootstrap.group(eventGroup)  //设置事件循环组（线程组）
                    .channel(NioSocketChannel.class)  //设置客户端通道实现类
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //设置自己的处理器handler
                            socketChannel.pipeline().addLast(new NettySimpleClientHandler());
                        }
                    });  //设置handler处理逻辑
            System.out.println("客户端启动...");

            //启动客户端去连接服务器
            ChannelFuture cf = clientBootstrap.connect(HOST, PORT).sync();
            //可以对future模式添加监听器，监听运行结果或关心事件
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (cf.isSuccess()) {
                    System.out.println("当前连接服务器 " + cf.channel().remoteAddress());
                } else {
                    System.out.println("客户端启动失败");
                }
            });
            //设置通道关闭监听
            cf.channel().closeFuture().sync();
        } finally {
            eventGroup.shutdownGracefully();
        }
    }

}
