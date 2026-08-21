package com.example.yourcarryourway.repository;

import com.example.yourcarryourway.domain.entities.User;
import com.example.yourcarryourway.domain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findFirstByRole(UserRole role);
}
