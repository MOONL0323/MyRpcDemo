package org.example.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcService {
    public static UserService userService = new UserServiceImpl();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8090);
        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(()->{
                ObjectOutputStream objectOutputStream = null;
                try {
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ObjectInputStream objectInputStream = null;
                try {
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    String id = (String) objectInputStream.readObject();
                    System.out.println("Received id: " + id);
                    User user = userService.getUserById(id);
                    objectOutputStream.writeObject(user);
                    objectOutputStream.flush();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
