package com.hanshin.supernova.community.infrastructure;

import com.hanshin.supernova.community.domain.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsByName(String name);

    @Query("SELECT c FROM Community c ORDER BY c.createdAt DESC")
    Page<Community> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT c FROM Community c ORDER BY c.createdAt ASC")
    Page<Community> findAllOrderByCreatedAtASC(Pageable pageable);
}
