package com.example.Vectify.Repository;

import com.example.Vectify.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Method to find a user by email, returning an Optional
    Optional<UserEntity> findByEmail(String email);

    // Updated method to find a user by username, returning an Optional
    Optional<UserEntity> findByUsername(String username);

    Optional<Object> findByEmailAndUserType(String email, String userType);

    List<UserEntity> findByUserType(String userType);
}
