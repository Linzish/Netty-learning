package me.unc.netty.nio.channel;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel应用实例1-本地文件写数据
 */
public class NioFileChannel01 {

    public static void main(String[] args) throws Exception {

        String str = "唱跳rap篮球";
        //创建一个输出流，并获取channel
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\learning/netty/channel/01-02.txt");

        //通过fileOutputStream获取对应的FileChannel
        //注意，这里的FileChannel实际类型是FileChannelImpl
        FileChannel channel = fileOutputStream.getChannel();

        //创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        //将str放入byteBuffer
        byteBuffer.put(str.getBytes());

        //反转buffer
        byteBuffer.flip();

        //将byteBuffer数据写入channel
        channel.write(byteBuffer);

        //关流
        fileOutputStream.close();

    }

}
