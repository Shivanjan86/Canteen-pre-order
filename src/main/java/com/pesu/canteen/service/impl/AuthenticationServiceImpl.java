package com.pesu.canteen.service.impl;

import com.pesu.canteen.dto.UserRegistrationDTO;
import com.pesu.canteen.model.entity.User;
import com.pesu.canteen.pattern.factory.UserFactory;
import com.pesu.canteen.repository.UserRepository;
import com.pesu.canteen.service.interfaces.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // The Spring Boot annotation goes on the Impl class!
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationDTO request) {
        // First, check if someone is already using this email
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("A user with this email is already registered!");
        }

        User user = userFactory.createUser(
                request.getName(),
                request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );
        
        // If the email is unique, save the user to the database
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        // Find the user by their email
        Optional<User> user = userRepository.findByEmail(email);

        // Check if the user exists and if the passwords match
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user.get(); // Login successful
        } else {
            throw new RuntimeException("Invalid email or password!");
        }
    }
}