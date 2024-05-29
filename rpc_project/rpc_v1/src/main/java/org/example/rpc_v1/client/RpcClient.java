package org.example.rpc_v1.client;

import org.example.rpc_v1.message.Request;
import org.example.rpc_v1.message.Response;
import org.example.rpc_v1.message.User;
import org.example.rpc_v1.service.UserService;

import java.io.IOException;

public class RpcClient {
    public static void main(String[] args) throws IOException {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1",8090);
        UserService proxy = clientProxy.getProxy(UserService.class);
        User user = proxy.getUserByUserId(10);
        System.out.println(user);
        User user1 = User.builder().id(2).username("test").password("test").build();
        Integer integer = proxy.insertUserId(user1);
        System.out.println(integer);
    }
}
