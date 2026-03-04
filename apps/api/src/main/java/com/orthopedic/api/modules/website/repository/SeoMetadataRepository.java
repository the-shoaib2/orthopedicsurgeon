package com.orthopedic.api.modules.website.repository;

import com.orthopedic.api.modules.website.entity.SeoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeoMetadataRepository extends JpaRepository<SeoMetadata, UUID> {
    Optional<SeoMetadata> findBySlug(String slug);
}
