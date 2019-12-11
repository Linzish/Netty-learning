package me.unc.netty.rpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;  //上下文
    private String result;  //返回的结果
    private String param;   //客户端调用方法时，传入的参数（远程调用的参数）

    //与服务器创建后被调用 ,第一个被带调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyClientHandler channelActive被调用");
        //在其他方法会使用到ctx
        context = ctx;
    }

    //收到服务器的数据后，调用该方法
    //涉及 notify 和 wait 操作都需要同步
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("NettyClientHandler channelRead被调用");
        result = msg.toString();
        //唤醒等待的线程，唤醒Callable的call
        notify();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    //被代理对象调用，发送数据=给服务器 -> wait -> 等待被（channelRead）唤醒 -> 返回结果
    @Override
    public synchronized Object call() throws Exception {
        System.out.println("call1 被调用");
        context.writeAndFlush(param);
        //等待结果，当被唤醒时，结果已经得到
        wait();
        System.out.println("call2 被调用");
        return result;  //服务方返回的结果
    }

    public void setParam(String param) {
        this.param = param;
    }
}
