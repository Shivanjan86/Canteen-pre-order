package com.pesu.canteen.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("STAFF")
public class Staff extends User {

    public Staff() {
        super();
    }

    public Staff(String name, String email, String password) {
        super(name, email, password);
    }
    
    // You can add staff-specific fields later if needed (e.g., shift timing)
}