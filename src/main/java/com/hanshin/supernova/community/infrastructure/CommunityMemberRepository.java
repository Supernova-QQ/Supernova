package com.hanshin.supernova.community.infrastructure;

import com.hanshin.supernova.community.domain.CommunityMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    List<CommunityMember> findAllByUserId(Long userId);
}
