package me.unc.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * Netty HTTP 服务器 自定义处理器
 * SimpleChannelInboundHandler 继承 ChannelInboundHandlerAdapter 有更多功能
 * HttpObject 是 Netty 封装的 Http基类
 */
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    /**
     * 当有读取事件时触发该事件
     * @param channelHandlerContext 上下文
     * @param httpObject Http信息，客户端和服务端通信的数据
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {

        //判断httpObject是否是httpRequest请求
        if (httpObject instanceof HttpRequest) {
            System.out.println("httpObject类型：" + httpObject.getClass());
            System.out.println("客户端地址：" + channelHandlerContext.channel().remoteAddress());

            //===========过滤特定项============
            //获取request
            HttpRequest httpRequest = (HttpRequest) httpObject;
            //获取uri
            URI uri = new URI(httpRequest.uri());
            if ("/xxxxx.ico".equals(uri.getPath())) {  //过滤特定向
                System.out.println("请求了 xxxxxx.ico，不做响应");
                return;
            }

            //回复信息给浏览器， 使用http协议
            ByteBuf content = Unpooled.copiedBuffer("hello，我是服务器", CharsetUtil.UTF_8);
            //构造一个httpResponse
            //设置协议版本和状态码200和回送内容
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            //设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");  //设置类型
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());  //设置长度

            //返回response
            channelHandlerContext.writeAndFlush(response);

        }

    }
}
