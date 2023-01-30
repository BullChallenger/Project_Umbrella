package com.umbrella.project_umbrella.repository;

import com.umbrella.project_umbrella.domain.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Long, User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
