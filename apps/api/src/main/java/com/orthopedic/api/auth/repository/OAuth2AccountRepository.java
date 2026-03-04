package com.orthopedic.api.auth.repository;

import com.orthopedic.api.auth.entity.OAuth2Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuth2AccountRepository extends JpaRepository<OAuth2Account, java.util.UUID> {
    Optional<OAuth2Account> findByProviderAndProviderId(String provider, String providerId);
}
