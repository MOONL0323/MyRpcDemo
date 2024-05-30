package org.example.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IOService implements Service{
    private final ThreadPoolExecutor threadPool;
    private ServiceProvider serviceProvider;
    public IOService(ServiceProvider serviceProvider){
        this.serviceProvider = serviceProvider;
        threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 1000, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
    }

    public IOService(ServiceProvider serviceProvider, int corePoolSize, int maximumPoolSize, int keepAliveTime, TimeUnit timeoutUnit, BlockingQueue<Runnable> workQueue){
        this.serviceProvider = serviceProvider;
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    }
    @Override
    public void start(int port) {
        System.out.println("IOService started");
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                Socket socket = serverSocket.accept();
                threadPool.execute(new WorkThread(socket, serviceProvider));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {

    }
}
