package com.orthopedic.api.modules.contact.repository;

import com.orthopedic.api.modules.contact.entity.NewsletterSubscriber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, UUID> {
    boolean existsByEmail(String email);

    Optional<NewsletterSubscriber> findByEmail(String email);

    Optional<NewsletterSubscriber> findByToken(String token);

    Page<NewsletterSubscriber> findByIsActiveTrue(Pageable pageable);
}
