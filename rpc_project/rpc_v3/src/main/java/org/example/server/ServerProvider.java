package org.example.server;


import java.util.HashMap;
import java.util.Map;

public class ServerProvider {
    private Map<String,Object> interfaceProvider;
    public ServerProvider(){
        interfaceProvider = new HashMap<>();
    }

    public void provideService(Object service){
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for(Class<?> i : interfaces){
            interfaceProvider.put(i.getName(),service);
        }
    }

    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

}
