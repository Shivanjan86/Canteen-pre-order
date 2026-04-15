package com.pesu.canteen.service.interfaces;

import com.pesu.canteen.dto.UserRegistrationDTO;
import com.pesu.canteen.model.entity.User;

public interface AuthenticationService {
    
    // Just the method signatures! No bodies.
    User registerUser(UserRegistrationDTO request);
    
    User loginUser(String email, String password);
}