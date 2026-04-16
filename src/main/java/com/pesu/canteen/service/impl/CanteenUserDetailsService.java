package com.pesu.canteen.service.impl;

import com.pesu.canteen.model.entity.Admin;
import com.pesu.canteen.model.entity.Staff;
import com.pesu.canteen.model.entity.User;
import com.pesu.canteen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanteenUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = resolveRole(user);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    private String resolveRole(User user) {
        String persistedRole = user.getRole();
        if (persistedRole != null && !persistedRole.isBlank()) {
            String normalizedRole = persistedRole.trim().toUpperCase();
            if ("ADMIN".equals(normalizedRole) || "STAFF".equals(normalizedRole)) {
                return normalizedRole;
            }
            if ("CUSTOMER".equals(normalizedRole) || "USER".equals(normalizedRole)) {
                return "CUSTOMER";
            }
        }

        if (user instanceof Admin) {
            return "ADMIN";
        }
        if (user instanceof Staff) {
            return "STAFF";
        }
        return "CUSTOMER";
    }
}
