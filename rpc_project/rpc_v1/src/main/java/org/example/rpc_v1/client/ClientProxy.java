package org.example.rpc_v1.client;

import lombok.AllArgsConstructor;
import org.example.rpc_v1.message.Request;
import org.example.rpc_v1.message.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    private String ip;
    private int port;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = Request.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args)
                .paramsTypes(method.getParameterTypes())
                .build();
        Response response = IOClient.sendRequest(ip, port, request);
        if(response.getCode() == 200){
            return response.getData();
        }else{
            throw new RuntimeException("调用失败："+response.getMessage());
        }

    }

    public <T> T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
        return (T) o;
    }
}
