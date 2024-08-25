package com.hanshin.supernova.hashtag.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hashtag")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "total_cnt")
    private int totalCnt;

    @Column(name = "daily_cnt")
    private int dailyCnt;

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
