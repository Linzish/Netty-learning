package me.unc.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol msg) throws Exception {
        //接收到数据并处理
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("============================");
        System.out.println("服务器接收到数据如下");
        System.out.println("长度：" + len);
        System.out.println("内容：" + new String(content, StandardCharsets.UTF_8));
        System.out.println("服务器收到的数据包量" + ++(this.count));
        System.out.println("============================" + "\n");

        //服务器回送数据给客户端，会送一个uuid
        String resp = UUID.randomUUID().toString();
        byte[] respBytes = resp.getBytes(StandardCharsets.UTF_8);
        int respLen = respBytes.length;

        //创建数据包对象
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(respLen);
        messageProtocol.setContent(respBytes);

        channelHandlerContext.writeAndFlush(messageProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        ctx.close();
    }
}
