package com.app.dev.bikeapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.dev.bikeapp.entity.User;

public abstract interface UserRepository extends JpaRepository<User,UUID>{
    
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @SQL("insert into users (name, email, phoneNumber, password, role) values (:name, :email, :phoneNumber, :password, :role)")
    User save(User user);

}
