package me.unc.netty.inout;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {
    //编码
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Long aLong, ByteBuf byteBuf) throws Exception {
        /*
        encoder会判断当前类型是不是该处理的类型，如果是则处理，如果不是则跳过encoder
         */
        System.out.println("MyLongToByteEncoder encode方法被调用");
        System.out.println("发送msg = " + aLong);
        byteBuf.writeLong(aLong);
    }
}
