package org.example.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.example.com.Request;
import org.example.com.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Data
@Builder
@AllArgsConstructor
public class RPCProxy implements InvocationHandler {
    private RPCClient rpcClient;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = Request.builder()
                .InterfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        Response response = rpcClient.sendRequest(request);
        if(response.getCode()==200){
            return response.getData();
        }else{
            System.out.println("remote call fail: "+response.getMessage());
            throw new RuntimeException("remote call fail: "+response.getMessage());
        }

    }

    public <T> T getProxy(Class<T> clazz) {
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
        return (T) o;
    }
}
