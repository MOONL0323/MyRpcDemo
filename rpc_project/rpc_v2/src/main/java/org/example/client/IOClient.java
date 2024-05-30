package org.example.client;

import lombok.Builder;
import lombok.Data;
import org.example.message.Request;
import org.example.message.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Data
@Builder
public class IOClient {
    public static Response sendRequest(String ip, int port, Request request) throws IOException {
        try{
            Socket socket = new Socket(ip,port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(request);
            oos.flush();
            System.out.println("Client send request: " + request);

            Response response = (Response) ois.readObject();
            System.out.println("Client receive response: " + response);
            return response;
        } catch (ClassNotFoundException e) {
            System.out.println("Client receive response error");
            throw new RuntimeException(e);
        }

    }
}
