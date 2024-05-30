package org.example.client;

import org.example.message.User;
import org.example.service.UserService;

public class RPCClient {
    public static void main(String[] args) {
        RPCProxy rpcProxy = new RPCProxy("localhost", 8090);
        UserService userService = rpcProxy.getProxy(UserService.class);
        User user = userService.getUserByName("test");
        System.out.println(user);
    }
}
