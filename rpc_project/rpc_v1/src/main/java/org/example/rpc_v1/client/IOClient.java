package org.example.rpc_v1.client;

import org.example.rpc_v1.message.Request;
import org.example.rpc_v1.message.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOClient {

    public static Response sendRequest(String ip, int port, Request request) throws IOException {
        try(Socket socket = new Socket(ip, port);){
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(request);
            oos.flush();
            System.out.println("客户端发送请求："+request);
            return (Response) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("从IO流中读取数据失败");
            throw new RuntimeException(e);
        }

    }
}
