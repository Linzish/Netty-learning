package me.unc.netty.inout;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Long aLong) throws Exception {
        System.out.println("服务器[" + channelHandlerContext.channel().remoteAddress() + "]回送的消息：" + aLong);
    }

    //重写channelActive发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler 发送数据");
        ctx.writeAndFlush(123456L);  //发送一个long
//        ctx.writeAndFlush(Unpooled.copiedBuffer());
    }
}
