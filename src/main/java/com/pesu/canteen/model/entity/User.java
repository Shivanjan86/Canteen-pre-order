package com.pesu.canteen.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")

// Enables single-table inheritance for Admin, Staff, Customer
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)

// Uses the "role" column to distinguish subclass types
@DiscriminatorColumn(name = "role")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String email;

    private String password;

    // Read-only view of JPA discriminator to resolve role consistently.
    @Column(name = "role", insertable = false, updatable = false)
    private String role;

    // Default constructor required by Hibernate
    public User() {
    }

    // Constructor used by subclasses like Customer
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
}