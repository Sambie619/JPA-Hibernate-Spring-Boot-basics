package com.sabin.repository;

import com.sabin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Used during login and task lookup.
    Optional<User> findByUsername(String username);

    // Used during registration to stop duplicate usernames.
    boolean existsByUsername(String username);

    // Used during registration to stop duplicate emails.
    boolean existsByEmail(String email);
}

