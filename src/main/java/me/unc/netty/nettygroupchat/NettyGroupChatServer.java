package me.unc.netty.nettygroupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Netty实现一个群聊系统服务端
 */
public class NettyGroupChatServer {

    private int PORT;

    public NettyGroupChatServer(int PORT) {
        this.PORT = PORT;
    }

    //run方法处理客户端请求
    public void run() throws InterruptedException {

        //创建主从eventLoop线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();

            server.group(bossGroup, workGroup)  //添加线程组
                    .channel(NioServerSocketChannel.class)  //添加channel类
                    .option(ChannelOption.SO_BACKLOG, 128)  //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //设置活动连接连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {  //使用通道初始化类设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //获取pipeline
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //添加处理器
                            pipeline.addLast("decoder", new StringDecoder());  //加入String解码器
                            pipeline.addLast("encoder", new StringEncoder());  //加入String编码器
                            //自己的业务处理handler
                            pipeline.addLast(new NettyGroupChatServerHandler());
                        }
                    });

            System.out.println("...服务器启动...");
            //绑定端口
            ChannelFuture channelFuture = server.bind(PORT).sync();
            channelFuture.addListener((ChannelFutureListener) cf -> {
                if (channelFuture.isSuccess()) {
                    System.out.println("当前监听接口 " + PORT);
                } else {
                    System.out.println("服务器启动失败");
                }
            });
            //监听关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //启动
        new NettyGroupChatServer(7777).run();

    }

}
