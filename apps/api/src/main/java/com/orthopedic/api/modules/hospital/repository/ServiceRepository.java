package com.orthopedic.api.modules.hospital.repository;

import com.orthopedic.api.modules.hospital.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, UUID> {
    List<ServiceEntity> findAllByHospitalId(UUID hospitalId);

    @Query("SELECT s FROM ServiceEntity s WHERE " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "s.status = 'ACTIVE'")
    List<ServiceEntity> searchServices(@Param("query") String query);

    List<ServiceEntity> findByIsFeaturedTrueAndStatus(ServiceEntity.ServiceStatus status);

    Page<ServiceEntity> findAllByStatus(ServiceEntity.ServiceStatus status,
            org.springframework.data.domain.Pageable pageable);
}
