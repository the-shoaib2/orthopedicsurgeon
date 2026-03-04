package com.orthopedic.api.auth.repository;

import com.orthopedic.api.auth.entity.TotpSecret;
import com.orthopedic.api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotpSecretRepository extends JpaRepository<TotpSecret, java.util.UUID> {
    Optional<TotpSecret> findByUser(User user);

    void deleteByUser(User user);
}
