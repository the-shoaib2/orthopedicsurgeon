package com.orthopedic.api.modules.website.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "seo_metadata")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String keywords;

    private String ogImage;

    private String canonicalUrl;
}
