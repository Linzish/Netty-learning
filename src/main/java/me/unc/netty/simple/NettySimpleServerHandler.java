package me.unc.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 自定义netty服务端的handler
 * 需要继承netty指定的某个HandlerAdapter(适配器)
 * 加入taskQueue 和 scheduleTaskQueue 示例
 */
public class NettySimpleServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取实际数据，这里读取客户端发送的信息
     * 当有数据读取时，该方法会被触发
     * @param ctx 上下文对象，含有 管道pipeline（业务处理管道），通道channel（数据读写通道），地址
     * @param msg 客户端发送的数据，默认object，根据实际情况转换
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //================简单示例==================
        //test
        System.out.println("server ctx = " + ctx);

        //将msg转成ByteBuf
        //这个ByteBuf是netty提供的，不是nio的，比nio的性能高
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("来自客户端 " + ctx.channel().remoteAddress() +
                "，消息：" + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址：" + ctx.channel().remoteAddress());


        //================== taskQueue示例 ======================

        //假设这里需要执行一个非常耗时的任务，则可以使用taskQueue
        //耗时任务-> 异步执行-> 提交到channel对应的NIOEventLoop 的taskQueue中
        //方案一 用户自定义的普通任务
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(10 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~，taskQueue中用户自定义的任务", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                System.out.println("发生异常" + e.getMessage());
            }
        });  //以上任务会被提交到channel对应的NIOEventLoop 的taskQueue中。（可以以设置多个）

        //测试没有阻塞
        System.out.println("go on..." + " 用户自定义的任务已异步执行");


        //=================== scheduleTaskQueue示例 =======================

        //用户自定义定时任务 -> 提交到scheduleTaskQueue
        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(20 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~，scheduleTaskQueue中用户自定义的定时任务", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                System.out.println("发生异常" + e.getMessage());
            }
        }, 5, TimeUnit.SECONDS);

        //测试没有阻塞
        System.out.println("go on..." + " 用户自定义的定时任务已异步执行");

    }

    /**
     * 数据读取完毕触发该方法
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //把数据信息写到缓冲区并刷新（write + flush）
        //还有设置相等的编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~", CharsetUtil.UTF_8));
    }

    /**
     * 处理异常
     * @param ctx 上下文
     * @param cause 异常
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //关闭
        ctx.close();
    }
}
