package org.example.service;

public class UserServiceImpl implements UserService{
    public User getUserById(String id) {
        return new User(id, "John Doe", "1234567890");
    }
}
