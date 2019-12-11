package me.unc.netty.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyMessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("MyMessageDecoder decode方法被调用");
        //需要将得到屙二进制字节码转行成MessageProtocol数据包对象
        int len = byteBuf.readInt();
        //按照len长度获取直接数组，重要！！
        byte[] content = new byte[len];
        byteBuf.readBytes(content);

        //封装成MessageProtocol，传到下一个handler进行业务处理
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(len);
        messageProtocol.setContent(content);

        list.add(messageProtocol);
    }
}
