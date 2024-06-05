package org.example.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.example.com.Request;
import org.example.com.Response;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
    private ServerProvider serverProvider;
    NettyServerHandler(ServerProvider serverProvider){
        this.serverProvider = serverProvider;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        Response response = getResponse(request);
        channelHandlerContext.writeAndFlush(response);
        channelHandlerContext.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private Response getResponse(Request request) {
        String interfaceName = request.getInterfaceName();
        Object service = serverProvider.getService(interfaceName);
        Method method = null;
        try{
            method =  service.getClass().getMethod(request.getMethodName(),request.getParamTypes());
            Object result = method.invoke(service,request.getParameters());
            return Response.builder().data(result).code(200).build();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
