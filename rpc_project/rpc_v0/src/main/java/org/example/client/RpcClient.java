package org.example.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8090);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject("123");
        objectOutputStream.flush();
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        try {
            System.out.println((org.example.service.User)objectInputStream.readObject());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
