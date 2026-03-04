package com.orthopedic.api.modules.website.repository;

import com.orthopedic.api.modules.website.entity.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, UUID> {
    List<Testimonial> findByIsFeaturedTrueAndIsVerifiedTrueOrderByCreatedAtDesc();

    Page<Testimonial> findByIsFeaturedTrueAndIsVerifiedTrue(Pageable pageable);

    Page<Testimonial> findAll(Pageable pageable);
}
