package com.hanshin.supernova.redis.hashtag_stat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hashtag_stats")
public class HashtagStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "tagger_identifier")
    private String taggerIdentifier;

    private LocalDate date;

    @Column(name = "hashtag_id")
    private Long hashtagId;
}
