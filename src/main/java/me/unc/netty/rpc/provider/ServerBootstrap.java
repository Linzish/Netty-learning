package me.unc.netty.rpc.provider;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.unc.netty.rpc.netty.NettyServer;

//ServerBootstrap 会启动netty服务端
public class ServerBootstrap {

    public static void main(String[] args) {

        NettyServer.startServer("127.0.0.1", 7777);

    }

}
