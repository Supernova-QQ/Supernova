package com.hanshin.supernova.community.infrastructure;

import com.hanshin.supernova.community.domain.Community;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsByName(String name);

    // 커뮤니티 ID로 이미지 URL 조회
    @Query("SELECT c.imgUrl FROM Community c WHERE c.id = :communityId")
    Optional<String> findImgUrlById(@Param("communityId") Long communityId);
}
