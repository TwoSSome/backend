package towssome.server;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import towssome.server.config.S3Config;

@EnableJpaAuditing
@SpringBootApplication
@Import(S3Config.class)
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
		//hello world
	}

	//Querydsl 사용을 위한 JPAQueryFactory 빈 등록
	@Bean
	public JPAQueryFactory jpaQueryFactory(EntityManager em) {
		return new JPAQueryFactory(em);
	}
}
