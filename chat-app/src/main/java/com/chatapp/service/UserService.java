package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    public void register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Пользователь уже существует");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

    public boolean login(String username, String password) {
        return userRepository.findByUsername(username)
                .map(u -> encoder.matches(password, u.getPassword()))
                .orElse(false);
    }

    public boolean exists(String username) {
        return userRepository.existsByUsername(username);
    }
}
