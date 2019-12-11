package me.unc.netty.nio.buffer;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * 分散与聚合测试，使用byteBuffer数组存字节流
 */
public class ScatteringAndGatheringTest {

    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        //绑定接口并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建byteBuffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接
        System.out.println("服务端启动...");
        SocketChannel accept = serverSocketChannel.accept();
        int msgLen = 8;

        while (true) {
            int byteRead = 0;
            while (byteRead < msgLen) {
                long read = accept.read(byteBuffers);
                byteRead += read;  //累计读取的字节流长度
                //打印信息
                Arrays.stream(byteBuffers).map(byteBuffer -> "position = " + byteBuffer.position() +
                        " limits = " + byteBuffer.limit()).forEach(System.out::println);
            }

            //将所有的buffer反转
            Arrays.stream(byteBuffers).forEach(Buffer::flip);

            //将数据读出显示到客户端
            long byteWrite = 0;
            while (byteWrite < msgLen) {
                long write = accept.write(byteBuffers);
                byteWrite += write;
            }

            //将所有的buffer重置
            Arrays.stream(byteBuffers).forEach(Buffer::clear);

            System.out.println("byteRead = " + byteRead + " byteWrite = " + byteWrite + " msgLen = " + msgLen);

        }

    }

}
