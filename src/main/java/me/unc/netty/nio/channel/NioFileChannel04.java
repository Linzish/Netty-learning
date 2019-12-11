package me.unc.netty.nio.channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Channel应用实例3-使用一个buffer完成文件的拷贝（transferFrom / transferTo）
 */
public class NioFileChannel04 {

    public static void main(String[] args) throws Exception {

        //获取文件输入流和channel
        FileInputStream fileInputStream = new FileInputStream(new File("D:\\learning/netty/channel/01-02.txt"));
        FileChannel inCh = fileInputStream.getChannel();

        //拷贝文件输出流和channel
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\learning/netty/channel/04.txt");
        FileChannel outCh = fileOutputStream.getChannel();

        //使用完成拷贝
//        outCh.transferFrom(inCh, 0, inCh.size());
        inCh.transferTo(0, inCh.size(), outCh);

        //关流
        fileInputStream.close();
        fileOutputStream.close();

    }

}
