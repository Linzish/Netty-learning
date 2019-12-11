package me.unc.netty.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel应用实例3-使用一个buffer完成文件的拷贝（read + write）
 */
public class NioFileChannel03 {

    public static void main(String[] args) throws Exception {

        //获取文件输入流和channel
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\learning/netty/channel/01-02.txt"));
        FileChannel inCh = fileInputStream.getChannel();

        //拷贝文件输出流和channel
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\learning/netty/channel/03.txt");
        FileChannel outCh = fileOutputStream.getChannel();

        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        while (true) { //循环读取
            //！！这个操作很重要，一定要重置buffer，否则read一直为0
            byteBuffer.clear();
            int read = inCh.read(byteBuffer);
            System.out.println("read = " + read);
            if (read == -1) {
                break;
            }
            //将buffer数据写入拷贝文件
            byteBuffer.flip(); //反转
            outCh.write(byteBuffer);
        }

        //关流
        fileInputStream.close();
        fileOutputStream.close();

    }

}
