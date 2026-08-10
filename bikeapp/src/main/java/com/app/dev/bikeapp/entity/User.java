package com.app.dev.bikeapp.entity;

import java.time.Instant;
import java.util.UUID;



import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name ="users")
public class User{

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Column(nullable = false) 
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)    
    private String email;

 
    @Column(nullable = false) 
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)    
    private Role role;

    @CreationTimestamp
    private Instant createdAt;
}