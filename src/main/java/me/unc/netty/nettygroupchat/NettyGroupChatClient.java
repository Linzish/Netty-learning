package me.unc.netty.nettygroupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * Netty实现一个群聊系统客户端
 */
public class NettyGroupChatClient {

    private final String host;
    private final int port;

    public NettyGroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws InterruptedException {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //加入相关的pipeline
                            pipeline.addLast("decoder", new StringDecoder());  //加入String解码器
                            pipeline.addLast("encoder", new StringEncoder());  //加入String编码器
                            //加入客户端handler
                            pipeline.addLast(new NettyGroupChatClientHandler());
                        }
                    });
            //连接
            ChannelFuture channelFuture = client.connect(host, port).sync();
            channelFuture.addListener((ChannelFutureListener) cf -> {
                if (channelFuture.isSuccess()) {
                    System.out.println("当前连接服务器 " + channelFuture.channel().remoteAddress());
                } else {
                    System.out.println("客户端启动失败");
                }
            });
            Channel channel = channelFuture.channel();
            System.out.println("--------" + channel.localAddress() + "--------");

            //扫描输入
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                //发送的数据
                String msg = scanner.nextLine();
                //通过channel发送
                channel.writeAndFlush(msg + "\r\n");
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        new NettyGroupChatClient("127.0.0.1", 7777).run();

    }

}
