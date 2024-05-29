package org.example.rpc_v1.service;

import org.example.rpc_v1.message.Request;
import org.example.rpc_v1.message.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class IOService {
    public static void main(String[] args) throws IOException {
        UserService userService = new UserServiceImpl();

        try {
            ServerSocket serverSocket = new ServerSocket(8090);
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("连接成功");
                new Thread(() -> {
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                        Request request = (Request) ois.readObject();
                        Method method = userService.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
                        Object result = method.invoke(userService, request.getParams());
                        oos.writeObject(Response.success(result));
                        oos.flush();


                    } catch (IOException | ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
