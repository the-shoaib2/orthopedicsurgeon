package com.orthopedic.api.auth.repository;

import com.orthopedic.api.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // NOTE: Do NOT cache User entities in Redis — the roles collection is a
    // Hibernate PersistentSet which cannot be deserialized outside an active
    // session.
    // Authentication queries are fast enough via DB with the @EntityGraph
    // eager-loading roles.
    @EntityGraph(attributePaths = { "roles" })
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String email, String firstName, String lastName, Pageable pageable);
}
