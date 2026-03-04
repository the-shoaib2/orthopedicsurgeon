package com.orthopedic.api.auth.repository;

import com.orthopedic.api.auth.entity.PasskeyCredential;
import com.orthopedic.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasskeyCredentialRepository extends JpaRepository<PasskeyCredential, String> {
    List<PasskeyCredential> findByUser(User user);

    Optional<PasskeyCredential> findByCredentialId(String credentialId);
}
