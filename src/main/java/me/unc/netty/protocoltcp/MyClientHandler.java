package me.unc.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

public class MyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol msg) throws Exception {
        //接受回送消息
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("============================");
        System.out.println("客户端接收到消息如下：");
        System.out.println("长度：" + len);
        System.out.println("内容：" + new String(content, StandardCharsets.UTF_8));
        System.out.println("客户端接收的消息量" + (++this.count));
        System.out.println("============================" + "\n");
    }

    //重写channelActive发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = "唱跳rap篮球";
        byte[] content = msg.getBytes(StandardCharsets.UTF_8);
        int length = content.length;
        //创建协议包对象
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(length);
        messageProtocol.setContent(content);

        //使用客户端发送10条数据
        for (int i = 0; i < 5; i++) {
            ctx.writeAndFlush(messageProtocol);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常" + cause.getMessage());
        ctx.close();
    }
}
