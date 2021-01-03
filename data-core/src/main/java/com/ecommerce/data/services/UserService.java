package com.ecommerce.data.services;

import com.ecommerce.data.entities.User;

public interface UserService {

    User findUserByLogin(String login);
}
