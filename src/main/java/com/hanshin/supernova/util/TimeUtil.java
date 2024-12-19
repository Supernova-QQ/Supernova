package com.hanshin.supernova.util;

import com.zaxxer.hikari.HikariDataSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private final HikariDataSource hikariDataSource;

    public TimeUtil(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    public void printTime() {
        // HikariCP 현재 시간
        LocalDateTime hikariTime = LocalDateTime.now(); // Hikari Time을 현재 시간으로 설정

        // 포맷팅하여 출력
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println("Hikari Time: " + hikariTime.format(formatter)); // 밀리초 포함

        // 서버 시간 출력
        System.out.println("Server Time: " + LocalDateTime.now().format(formatter)); // 서버 시간도 밀리초 포함
    }
}
