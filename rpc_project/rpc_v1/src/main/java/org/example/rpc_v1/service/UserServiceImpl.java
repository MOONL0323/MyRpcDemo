package org.example.rpc_v1.service;

import org.example.rpc_v1.message.User;

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
