package org.example.client;

import org.example.com.User;
import org.example.service.BlogService;
import org.example.service.UserService;

public class TestClient {
    public static void main(String[] args) {
        RPCClient rpcClient = new NettyRPCClient("127.0.0.1",8090);
        RPCProxy rpcProxy = new RPCProxy(rpcClient);
        UserService userService = rpcProxy.getProxy(UserService.class);
        User user = userService.getUserByName("111");
        System.out.println(user);
        BlogService blogService = rpcProxy.getProxy(BlogService.class);
        System.out.println(blogService.getBlogById(6));
    }
}
