package com.hanshin.supernova;

import com.hanshin.supernova.util.TimeUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SupernovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupernovaApplication.class, args);

		// HikariCP 데이터소스 설정
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/supernova");
		hikariConfig.setUsername("root");
		hikariConfig.setPassword("1234");

		HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);

		// TimeUtil 객체 생성 및 시간 출력
		TimeUtil timeUtil = new TimeUtil(hikariDataSource);
		timeUtil.printTime();

		// HikariCP 데이터소스 닫기
		hikariDataSource.close();

	}
}
