package org.example.client;

import lombok.AllArgsConstructor;
import org.example.message.Request;
import org.example.message.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class RPCProxy implements InvocationHandler {
    private String ip;
    private int port;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = Request.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .parameterTypes(method.getParameterTypes())
                .build();

        Response response = IOClient.sendRequest(ip, port, request);
        if(response.getCode() == 500) {
            throw new RuntimeException(response.getMessage());
        }
        return response.getData();
    }
    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
        return (T) o;
    }
}
