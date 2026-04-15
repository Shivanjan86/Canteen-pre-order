package com.pesu.canteen.pattern.factory;

import com.pesu.canteen.model.entity.Admin;
import com.pesu.canteen.model.entity.Staff;
import com.pesu.canteen.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createUser(String name, String email, String password, String role) {
        String normalizedRole = role == null ? "CUSTOMER" : role.trim().toUpperCase();

        return switch (normalizedRole) {
            case "ADMIN" -> new Admin(name, email, password);
            case "STAFF" -> new Staff(name, email, password);
            default -> new User(name, email, password);
        };
    }
}
