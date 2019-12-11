package me.unc.netty.rpc.customer;

import me.unc.netty.rpc.netty.NettyClient;
import me.unc.netty.rpc.serviceinterface.HelloService;

public class ClientBootstrap {

    //定义协议头
    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) {

        //创建一个消费者
        NettyClient customer = new NettyClient();
        //创建代理对象
        HelloService service = (HelloService) customer.getRPC(HelloService.class, providerName);

        //通过代理对象调用方法提供者的方法
        String res = service.hello("您好，我的RPC~");
        System.out.println("调用的结果 res = " + res);

    }

}

