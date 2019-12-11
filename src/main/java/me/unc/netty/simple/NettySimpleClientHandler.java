package me.unc.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

/**
 * 自定义netty客户端的handler
 */
public class NettySimpleClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当管道就绪时就会触发该方法
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //test
        System.out.println("client ctx : " + ctx);
        //发送信息到服务端
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello server~", CharsetUtil.UTF_8));
    }

    /**
     * 当通道有读取事件时，会触发该方法
     * @param ctx 上下文
     * @param msg 消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        //接受服务器的消息
        System.out.println("来自服务器 " + ctx.channel().remoteAddress() +
                "，消息：" + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("服务器的地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 异常处理
     * @param ctx 上下文
     * @param cause 异常
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //关闭
        ctx.close();
    }
}
