package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.example.com.Request;
import org.example.com.Response;

public class NettyRPCClient implements RPCClient{
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private String host;
    private int port;
    public NettyRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }
    @Override
    public Response sendRequest(Request request) {
        try{
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().writeAndFlush(request);
            channelFuture.channel().closeFuture().sync();
            AttributeKey<Response> key = AttributeKey.valueOf("RPCResponse");
            Response response = channelFuture.channel().attr(key).get();
            System.out.println("response: "+response);
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
