package org.example.service;

import org.example.message.Request;
import org.example.message.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class WorkThread implements Runnable{
    private Socket socket;
    private ServiceProvider serviceProvider;
    public WorkThread(Socket socket, ServiceProvider serviceProvider){
        this.socket = socket;
        this.serviceProvider = serviceProvider;
    }


    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Request request = (Request) ois.readObject();
            Response response = getResponse(request);
            oos.writeObject(response);
            oos.flush();
            System.out.println("Server response: " + response);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Response getResponse(Request request) {
        String interfaceName = request.getInterfaceName();
        Object service = serviceProvider.getService(interfaceName);
        Method method = null;
        try{
            method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            Object result = method.invoke(service, request.getParameters());
            return Response.success(result);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
