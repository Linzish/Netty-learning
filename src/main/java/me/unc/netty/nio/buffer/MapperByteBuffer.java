package me.unc.netty.nio.buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 映射字节流测试，可以在内存直接修改文件
 */
public class MapperByteBuffer {

    public static void main(String[] args) throws Exception {

        //随机文件读写类，提供随机文件读写功能
        RandomAccessFile file = new RandomAccessFile("D:\\learning/netty/buffer/1.txt", "rw");
        //获取对应的通道
        FileChannel channel = file.getChannel();

        /*
        参数1：FileChannel.MapMode.READ_WRITE 使用读写模式
        参数2：可以直接修改的其起始位置
        参数3：映射到内存的大小（注意不是索引）（可以修改6-10）
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 6, 5);
        //修改
        mappedByteBuffer.put(0, (byte) 'W');
        mappedByteBuffer.put(1, (byte) 'O');
        mappedByteBuffer.put(2, (byte) 'R');

        file.close();
    }

}
