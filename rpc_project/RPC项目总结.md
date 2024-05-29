# RPC项目总结

[TOC]



## 版本v1

### 回顾问题：

我们在v0版本采用的是直接利用socket通信放回我们想要的信息结果，但是有很明显的问题存在：我们只能调用接口中的一个方法，返回值确定的，定死了。甚至我们要传过去参数。总之就是十分固定，一点拓展性都没有。

### 需求：

要做出客户端可以调用接口中的不同方法，过程对用户透明，理论上我们只要指定我们要调用哪个接口的哪个方法，然后传入参数即可。

### 框架图：

![image-20240529103553658](G:\rpc_project\readme_picture\image-20240529103553658.png)

这次我们对客户端和服务端进行了封装，提供序列化反序列化。消息的格式也进行了封装，底层采用BIO进行传输，用户在调用过程中是感觉不到的，看起来就像是直接调用了服务端的方法一样。

### 前置知识：

1.BIO  2.反射  3动态代理

---

### 具体代码过程：

首先我们写公共部分，首先定义消息格式：

Request格式

```java
@Data
@Builder
public class Request implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsTypes;
 }
```

解释：因为我们要做到客户端要告诉服务端，我们要调用哪个接口，接口中的哪个方法，参数类型以及参数列表是什么。



Response格式

```java
@Data
@Builder
public class Response implements Serializable {
    private int code;
    private String message;
    private Object data;

    public static Response success(Object data){
        return Response.builder().code(200).message("调用成功").data(data).build();
    }

    public static Response fail(String message){
        return Response.builder().code(500).message(message).build();
    }

}
```

解释：服务端需要返回状态码，状态信息和调用方法之后所放回的数据。



实体类，比如这里的User

```java
@Data
@Builder
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
}
```

---

接下来，我们按照框架图一步一步往下写：

我们来到最底层的IO传输，IOClient要做的就是传request过去，拿response回来，IOService要做的就是拿到request分析完之后，将request向上层传递，然后拿到上层传下来的结果封装成response返回回去。

IOClient

```java
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
```

服务端复杂一点，因为我们这个将调用具体的方法也给写一块了



IOService

```java
public class IOService {
    public static void main(String[] args) throws IOException {
        UserService userService = new UserServiceImpl();

        try {
            ServerSocket serverSocket = new ServerSocket(8090);
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("连接成功");
                new Thread(() -> {
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                        Request request = (Request) ois.readObject();
                        Method method = userService.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
                        Object result = method.invoke(userService, request.getParams());
                        oos.writeObject(Response.success(result));
                        oos.flush();


                    } catch (IOException | ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

注意这句话：

![image-20240529145915206](G:\rpc_project\readme_picture\image-20240529145915206.png)

其实就是框架图中的返回给上层，然后上层调用方法之后返回结果给IO层。所以这代码应该叫做RPCService，因为实际包含了两层的内容

然后最后，我们补上具体的要调用的接口和实现类即可：



UserService——业务接口

```java
public interface UserService {
    User getUserByUserId(Integer id);
    Integer insertUserId(User user);
}
```

UserServiceImpl——具体的业务实现类

```java
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        return User.builder().id(id).username("test").password("test").build();
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入成功："+user);
        return 1;
    }
}
```

---

我们似乎已经完成了整个过程，那我们直接写用户如何调用即可了

我们尝试写一下：

```java
public class RPCClient{
    public static void main(String[] args){
        Request request = Request.builder()
            .methodName("getUserByUserId")
            .params(new Object[]{1}).paramsTypes(new Class[]{Integer.class}).build();
        
        Response response = IOClient.sendRequest("127.0.0.1",8090,request);
        User data = (User) response.getData();
    }
}
```

emm...好像没有多大毛病，但是这里只是调用了接口中的一个方法，我们来调用另一个方法看看

```java
public class RPCClient{
    public static void main(String[] args){
        Request request1 = Request.builder().methodName("insertUserId").params(new Object[]{User.builder().id(2).username("test").password("test").build()}).paramsTypes(new Class[]{User.class}).build();
        
        Response response1 = IOClient.sendRequest("127.0.0.1",8090,request1);
        Integer data1 = (Integer) response1.getData();
    }
}
```

emm...好像乍看没什么毛病，但是我们发现其实对用户不透明。什么意思呢，我们要自己封装request，我们要做到的是可以像本地一样的，类似，

xxx.xxxmethod(xxx)的形式。

也就是做到像这样的，同一个对象去执行，利用动态代理模式，

```java
public class RPCClient{
    public static void main(String[] args){
        ...
        
        proxy.getUserByUserId(1);
        proxy.inserUserId(user);
    }
}
```

也就是我们要对以上过程封装，很简单，封装以上几步：

```java
		//1.封装request
		Request request= xxx;
        //2.IO传输
        Response response = IOClient.sendRequest("127.0.0.1",8090,request);
        //3.调用具体类返回结果
        response.getData();
```

需要注意的是，这个封装要一般话，什么意思呢，不同的方法用的代码是同一套，这很简单，利用发射，所以我们利用动态代理封装一下

```java
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
```

动态代理执行后，每次我们调用getProxy方法得到一个Proxy代理类实例后，都会去调用invoke方法，执行封装reques，底层IO发送，封装response，返回结果。

我们重新写一下我们的用户如何调用：

```java
public class RpcClient {
    public static void main(String[] args) throws IOException {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1",8090);
        UserService proxy = clientProxy.getProxy(UserService.class);
        User user = proxy.getUserByUserId(10);
        System.out.println(user);
        User user1 = User.builder().id(2).username("test").password("test").build();
        Integer integer = proxy.insertUserId(user1);
        System.out.println(integer);
    }
}
```

### 结果：

![image-20240529161838177](G:\rpc_project\readme_picture\image-20240529161838177.png)

<img src="G:\rpc_project\readme_picture\image-20240529161515070.png" alt="image-20240529161515070" style="zoom:200%;" /

![image-20240529161729572](G:\rpc_project\readme_picture\image-20240529161729572.png)

### 总结：

1.使用了更加通用的消息格式，reques，respone更加清晰

2.框架层次结构更加清晰，通过代理模式可以调用服务的不同方法

3.客户端耦合度降低，不与特定ip端口服务绑定

### 问题：

1.只能调用一个服务（接口）里面的方法，我们在服务端是写死在里面的，所以要解决可以注册更多的服务（接口）

2.层次耦合度还是太高，看看能不能松耦

3.BIO效率太低了，每次都是阻塞的，看看怎么提升

---

