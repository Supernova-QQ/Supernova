package com.hanshin.supernova.bookmark.infrastructure;

import com.hanshin.supernova.bookmark.domain.Bookmark;
import com.hanshin.supernova.bookmark.domain.BookmarkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // 특정 유저가 북마크한 질문 리스트 조회
    List<Bookmark> findByUserIdAndCommIdAndType(Long userId, Long commId, BookmarkType type);

    boolean existsByUserIdAndCommIdAndTargetIdAndType(Long userId, Long commId, Long targetId, BookmarkType type);

    // 특정 유저의 특정 타겟 ID와 타입으로 북마크 검색
    Optional<Bookmark> findByUserIdAndCommIdAndTargetIdAndType(Long userId, Long commId, Long targetId, BookmarkType type);

}
