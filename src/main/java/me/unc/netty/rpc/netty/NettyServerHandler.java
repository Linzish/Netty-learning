package me.unc.netty.rpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.unc.netty.rpc.provider.HelloServiceImpl;

//服务器handler比较简单
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取客户端发送的消息，并调用服务
        System.out.println("msg = " + msg);
        //客户端在调用rpc api时需要定义一个协议
        //比如每次发消息时都以某个字符串开头 "HelloService#hello#"
        if (msg.toString().startsWith("HelloService#hello#")) {
            //服务端响应返回的字符串
            String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            ctx.writeAndFlush(result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
