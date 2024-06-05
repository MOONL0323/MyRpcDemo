package org.example.service;

import org.example.com.User;

public class UserServiceImpl implements UserService {
    @Override
    public User getUserByName(String name) {
        return User.builder().name(name).age(18).build();
    }
}
