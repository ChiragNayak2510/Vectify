package com.example.Vectify.Service;

import com.example.Vectify.Entity.UserEntity;
import com.example.Vectify.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Get all users
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Get a user by ID
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Add or update a user
    public UserEntity addOrUpdateUser(UserEntity user) {
        // Check if the user already exists by email
        Optional<UserEntity> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            UserEntity existing = existingUser.get();
            existing.setUsername(user.getUsername());
            existing.setUserType(user.getUserType()); // Update userType
            return userRepository.save(existing); // Update user
        } else {
            return userRepository.save(user); // Add new user
        }
    }

    // Check if a user exists by username
    public boolean userExistsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Get a user by username
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Delete a user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Get a user by email
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Check if a user exists by email
    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public List<UserEntity> getUsersByUserType(String userType) {
        return userRepository.findByUserType(userType);
    }

    public boolean userExistsByEmailAndUserType(String email, String userType) {
        return userRepository.findByEmailAndUserType(email, userType).isPresent();
    }
}
