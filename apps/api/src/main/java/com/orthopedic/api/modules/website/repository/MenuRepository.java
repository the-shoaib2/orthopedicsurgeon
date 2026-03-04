package com.orthopedic.api.modules.website.repository;

import com.orthopedic.api.modules.website.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findByTypeAndParentIsNullAndIsActiveTrueOrderByOrderAsc(String type);
}
