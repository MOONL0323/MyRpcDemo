package org.example.server;

import org.example.service.BlogService;
import org.example.service.BlogServiceImpl;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;

public class TestRPCServer {
    public static void main(String[] args) {
        ServerProvider serverProvider = new ServerProvider();
        serverProvider.provideService(new UserServiceImpl());
        serverProvider.provideService(new BlogServiceImpl());
        RPCServer rpcServer = new NettyRPCServer(serverProvider);
        rpcServer.start(8090);
    }
}
