package me.unc.netty.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel应用实例2-本地文件读数据
 */
public class NioFileChannel02 {

    public static void main(String[] args) throws Exception {

        //创建文件输入流
        File file = new File("D:\\learning/netty/channel/01-02.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        //通过fileInputStream获取对应的channel
        FileChannel channel = fileInputStream.getChannel();

        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length() + 2);

        //将通道数据读取到byteBuffer
        channel.read(byteBuffer);

        //打印
        System.out.println("文件数据：" + new String(byteBuffer.array()));
    }

}
