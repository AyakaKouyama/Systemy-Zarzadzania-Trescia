package com.ecommerce.data.services;

import com.ecommerce.data.entities.User;
import com.ecommerce.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }
}
