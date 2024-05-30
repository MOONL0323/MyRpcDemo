package org.example.service;


public class RPCService {
   public static void main(String[] args) {
       UserService userService = new UserServiceImpl();
       BlogService blogService = new BlogServiceImpl();

       ServiceProvider serviceProvider = new ServiceProvider();
       serviceProvider.provideServiceInterface(userService);
       serviceProvider.provideServiceInterface(blogService);

       IOService ioService = new IOService(serviceProvider);
       ioService.start(8090);

   }
}
