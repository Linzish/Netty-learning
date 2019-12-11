package me.unc.netty.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {

    //创建线程池
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //
    private static NettyClientHandler handler;

    //创建代理对象
    //编写方法，使用代理模式，获取一个代理对象
    public Object getRPC(final Class<?> serviceClass, final String providerName) {
        //反射获取代理
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serviceClass}, (proxy, method, args) -> {
            //下面代码，只有客户端每调用一次都会执行一次
            if (handler == null) {
                initClient();
            }
            //设置要发送给服务端的信息 协议头+信息 , 就是客户端调用api hello(???)
            handler.setParam(providerName + args[0]);

            return executor.submit(handler).get();
        });
    }

    //初始化客户端
    private static void initClient(){
        handler = new NettyClientHandler();
        EventLoopGroup eventGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(handler);
                    }
                });
        try {
            bootstrap.connect("127.0.0.1", 7777).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
