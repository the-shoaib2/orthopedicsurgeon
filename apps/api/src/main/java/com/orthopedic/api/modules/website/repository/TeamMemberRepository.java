package com.orthopedic.api.modules.website.repository;

import com.orthopedic.api.modules.website.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    List<TeamMember> findByShowOnWebsiteTrueOrderByDisplayOrderAsc();
}
