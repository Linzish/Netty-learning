package me.unc.netty.protocoltcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //添加message的编码器
                            pipeline.addLast(new MyMessageEncoder());
                            //添加message的解码器
                            pipeline.addLast(new MyMessageDecoder());
                            //添加自定义handler
                            pipeline.addLast(new MyClientHandler());
                        }
                    });
            //连接
            ChannelFuture channelFuture = client.connect("127.0.0.1", 7777).sync();
            channelFuture.addListener((ChannelFutureListener) cf -> {
                if (channelFuture.isSuccess()) {
                    System.out.println("当前连接服务器 " + channelFuture.channel().remoteAddress());
                } else {
                    System.out.println("客户端启动失败");
                }
            });
            Channel channel = channelFuture.channel();
            System.out.println("--------" + channel.localAddress() + "--------");

        } catch (Exception e) {
            group.shutdownGracefully();
        }

    }

}
