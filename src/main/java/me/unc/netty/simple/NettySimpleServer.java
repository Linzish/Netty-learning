package me.unc.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 简易netty服务端
 */
public class NettySimpleServer {

    private static final int PORT = 6668;

    public static void main(String[] args) throws Exception {
        //创建和
        //1.创建按两个线程组bossGroup 和 workGroup
        //2.bossGroup只处理连接请求，真正的和客户端业务处理，会交给完成workGroup
        //3.两个都是无限循环处理业务
        //4.bossGroup 和 workGroup含有的子线程（NioEventLoop）个数为实际cpu核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //创建服务端启动对象，并配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //使用链式编程进行设置
            serverBootstrap.group(bossGroup, workGroup)  //设置主从两个线程组
                    .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel 作为服务端的通道实现（反射）
                    .option(ChannelOption.SO_BACKLOG, 128)  //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //设置活动连接连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {  //创建一个通道初始化对象
                        //向workGroup的 管道设置处理器handler
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //向管道设置自定义的handler
                            socketChannel.pipeline().addLast(new NettySimpleServerHandler());
                        }
                    });  //给我们的workGroup 的 EventLoopGroup对应的管道设置对应的处理器

            System.out.println("...服务器启动...");

            //给服务端绑定端口，
            // 并且同步处理，会生成一个ChannelFuture对象（future模式）
            ChannelFuture cf = serverBootstrap.bind(PORT).sync();
            //可以对future模式添加监听器，监听运行结果或关心事件
            cf.addListener((ChannelFutureListener) channelFuture -> {
                if (cf.isSuccess()) {
                    System.out.println("当前监听接口 " + PORT);
                } else {
                    System.out.println("服务器启动失败");
                }
            });
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            //优雅关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
