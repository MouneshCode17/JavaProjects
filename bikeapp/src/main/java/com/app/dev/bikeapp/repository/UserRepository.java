package com.app.dev.bikeapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.dev.bikeapp.entity.User;

public abstract interface UserRepository extends JpaRepository<User,UUID>{
    
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @SQL("insert into users (name, email, password, role) values (:name, :email, :password, :role)")
    User save(User user);

}
