package com.example.Vectify.Repository;

import com.example.Vectify.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    // Custom query methods (if needed) can be defined here, for example:
    UserEntity findByEmail(String email);
}


