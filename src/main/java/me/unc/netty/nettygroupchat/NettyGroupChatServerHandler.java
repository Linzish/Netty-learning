package me.unc.netty.nettygroupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Netty群聊系统服务端handler
 */
public class NettyGroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组，管理所有的channel
    //GlobalEventExecutor.INSTANCE -> 是一个单例全局的事件执行器，后面能自动删除退出channel因为有它
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    /**
     * handlerAdded方法会在连接建立时立刻触发，一旦连接，第一个执行
     * 用于把当前连接的channel加入到channels中管理
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将客户端加入群聊推送到其他杂在线客户端
        //因为channels管理着一个channel集合，所以该方法会自动遍历执行
        channels.writeAndFlush("客户端[" + channel.remoteAddress() + "] 加入群聊 " + sdf.format(new Date()) +"\n");
        //加入管理组
        channels.add(channel);
    }

    /**
     * 表示连接断开， 可以提示xxx 离线
     * 触发这个方法时，channels会自动删除当前断开的channel
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //将某个客户端离开的信息
        Channel channel = ctx.channel();
        channels.writeAndFlush("客户端[" + channel.remoteAddress() + "] 退出群聊\n");
    }

    /**
     * 表示channel处于活动状态 ，可以提示xxx 上线
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //上面方法已经写了提示，这里旨在服务端打印一下
        System.out.println(ctx.channel().remoteAddress() + " 上线");
    }

    /**
     * 表示channel处于不活动状态，可以提示xxx 下线
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 下线");
    }

    /**
     * 读取数据，转发消息
     * @param channelHandlerContext 上下文
     * @param s 信息
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        //获取当前channel
        Channel channel = channelHandlerContext.channel();
        //排除自己转发
        channels.forEach(ch -> {
            if (channel != ch) {
                //不是当前的channel，转发消息
                ch.writeAndFlush("客户端[" + ch.remoteAddress() + "] : " + s + "\n");
            } else {
                //自己看到的消息
                ch.writeAndFlush("我 : " + s + "\n");
            }
        });
    }

    /**
     * 处理异常
     * @param ctx 上下文
     * @param cause 异常
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
