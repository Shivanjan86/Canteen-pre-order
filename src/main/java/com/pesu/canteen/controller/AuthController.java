package com.pesu.canteen.controller;

import com.pesu.canteen.dto.AuthUserDTO;
import com.pesu.canteen.dto.UserRegistrationDTO;
import com.pesu.canteen.model.entity.Admin;
import com.pesu.canteen.model.entity.Staff;
import com.pesu.canteen.model.entity.User;
import com.pesu.canteen.service.interfaces.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Tells Spring this class handles web requests
@RequestMapping("/api/auth") // All endpoints here will start with /api/auth
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    // POST request to /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO request) {
        try {
            User savedUser = authService.registerUser(request);
            return ResponseEntity.ok(toAuthUserDTO(savedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST request to /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            User loggedInUser = authService.loginUser(email, password);
            return ResponseEntity.ok(toAuthUserDTO(loggedInUser));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    private AuthUserDTO toAuthUserDTO(User user) {
        AuthUserDTO dto = new AuthUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        if (user instanceof Admin) {
            dto.setRole("ADMIN");
        } else if (user instanceof Staff) {
            dto.setRole("STAFF");
        } else {
            dto.setRole("CUSTOMER");
        }

        return dto;
    }
}