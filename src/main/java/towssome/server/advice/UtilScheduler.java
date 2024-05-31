package towssome.server.advice;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import towssome.server.entity.EmailVerification;
import towssome.server.entity.QMember;
import towssome.server.entity.QRefreshToken;
import towssome.server.repository.EmailVerificationRepository;

import java.time.LocalDateTime;
import java.util.List;

import static towssome.server.entity.QMember.*;
import static towssome.server.entity.QRefreshToken.*;

@Component
@RequiredArgsConstructor
public class UtilScheduler {

    private final JPAQueryFactory queryFactory;
    private final EmailVerificationRepository emailVerificationRepository;

    /**
     * 매주 월요일 오전 1시에 랭크포인트 초기화
     */
    @Scheduled(cron = "0 0 1 ? * MON")
    public void initializationRank() {

        queryFactory.update(member)
                .set(member.rankPoint, 0)
                .execute();
    }

    /**
     * 리프레쉬 토큰 삭제
     */
    @Scheduled(cron = "0 0 1 ? * MON")
    public void deleteRefreshToken() {

        queryFactory.delete(refreshToken)
                .execute();

    }

    /**
     * 이메일 인증 엔티티 삭제
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void deleteOldEntities() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(1);
        List<EmailVerification> result = emailVerificationRepository.findByCreateDateBefore(fiveMinutesAgo);
        emailVerificationRepository.deleteAll(result);
    }

}
