package com.orthopedic.api.modules.contact.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "newsletter_subscribers", indexes = {
        @Index(name = "idx_newsletter_token", columnList = "token"),
        @Index(name = "idx_newsletter_email", columnList = "email")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", length = 254, nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt;

    @Column(name = "unsubscribed_at")
    private LocalDateTime unsubscribedAt;

    @Column(name = "token", length = 100, nullable = false, unique = true)
    private String token;

    @PrePersist
    protected void onCreate() {
        if (subscribedAt == null)
            subscribedAt = LocalDateTime.now();
        if (isActive == null)
            isActive = false;
        if (token == null)
            token = UUID.randomUUID().toString().replace("-", "");
    }
}
