package me.unc.netty.rpc.provider;

import me.unc.netty.rpc.serviceinterface.HelloService;

public class HelloServiceImpl implements HelloService {
    //当有消费方调用方法时返回结果
    @Override
    public String hello(String msg) {
        System.out.println("HelloService 收到客户端消息 = " + msg);
        //根据msg返回不同的数据
        if (msg != null) {
            return "你好客户端，我已经收到您的消息 【" + msg + "】";
        } else {
            return "你好客户端，我已经收到您的消息";
        }
    }
}
