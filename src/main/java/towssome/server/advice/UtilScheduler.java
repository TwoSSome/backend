package towssome.server.advice;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import towssome.server.entity.QMember;
import towssome.server.entity.QRefreshToken;

import static towssome.server.entity.QMember.*;
import static towssome.server.entity.QRefreshToken.*;

@Component
@RequiredArgsConstructor
public class UtilScheduler {

    private final JPAQueryFactory queryFactory;

    /**
     * 매주 월요일 오전 1시에 실행
     */
    @Scheduled(cron = "0 0 1 ? * MON")
    public void initializationRank() {

        queryFactory.update(member)
                .set(member.rankPoint, 0)
                .execute();


    }

    @Scheduled(cron = "0 0 1 ? * MON")
    public void deleteRefreshToken() {

        queryFactory.delete(refreshToken)
                .execute();

    }

}
