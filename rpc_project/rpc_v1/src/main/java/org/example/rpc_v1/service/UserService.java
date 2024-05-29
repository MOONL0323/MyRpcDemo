package org.example.rpc_v1.service;

import org.example.rpc_v1.message.User;

public interface UserService {
    User getUserByUserId(Integer id);
    Integer insertUserId(User user);
}
