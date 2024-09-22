package towssome.server.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.entity.Member;

import java.util.List;

import static towssome.server.entity.QMember.*;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findRanker(int size) {

        return queryFactory.selectFrom(member)
                .orderBy(member.rankPoint.desc())
                .limit(size)
                .fetch();

    }
}
