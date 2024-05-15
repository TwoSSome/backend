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

	// python 서버 동시 실행
	@PostConstruct
    public void init() {
		String scriptPath = "python/hashtagmaker.py";
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
        try {
            Process p = pb.start();
            // 로그 출력을 위한 스트림 리더
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            p.waitFor(); // 프로세스 종료 대기
            System.out.println("Python 스크립트 실행 종료, 상태 코드: " + p.exitValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
