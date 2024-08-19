package com.hanshin.supernova.hashtag.domain;

import com.hanshin.supernova.hashtag.infrastructure.HashtagRepository;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "hashtag")
public class Hashtag {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

//    @Column(name = "total_cnt")
//    private int totalCnt;
//
//    @Column(name = "daily_cnt")
//    private int dailyCnt;

//    private int cnt;
//
//    public void addCnt() {
//        this.cnt++;
//    }
//
//    public void removeCnt() {
//        this.cnt--;
//    }
}
