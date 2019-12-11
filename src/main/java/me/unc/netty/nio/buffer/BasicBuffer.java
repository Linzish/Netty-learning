package me.unc.netty.nio.buffer;

import java.nio.IntBuffer;

public class BasicBuffer {

    public static void main(String[] args) {

        //创建一个buffer，大小为5，即可以存5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //capacity()方法返回buffer的长度
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        //反转buffer，写变读
        intBuffer.flip();

        //循环输出
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }

    }

}
