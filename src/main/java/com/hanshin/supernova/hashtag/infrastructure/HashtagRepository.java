package com.hanshin.supernova.hashtag.infrastructure;

import com.hanshin.supernova.hashtag.domain.Hashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    boolean existsByName(String hashtagName);

    Hashtag findByName(String hashtagName);

    List<Hashtag> findByNameContaining(String hashtagName);
}
