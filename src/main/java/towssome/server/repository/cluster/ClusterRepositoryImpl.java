package towssome.server.repository.cluster;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import towssome.server.entity.Member;

import java.util.List;

import static towssome.server.entity.QCluster.cluster;
import static towssome.server.entity.QMember.member;
import static towssome.server.entity.QSubscribe.subscribe;

@Repository
@RequiredArgsConstructor
public class ClusterRepositoryImpl implements ClusterRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<Member> findClustersByMemberId(Long memberId){
        List<Member> results;
        results = queryFactory.select(cluster.member)
                .from(cluster)
                .where(cluster.clusterNum.eq(
                        queryFactory.select(cluster.clusterNum)
                                .from(cluster)
                                .where(cluster.member.id.eq(memberId))
                ))
                .where(cluster.member.id.ne(memberId))
                .fetch();

        return results;
    }

    @Override
    public List<Member> findClustersExceptingMemberAndFollowing(Long memberId){
        List<Member> results;
        results = queryFactory.select(cluster.member)
                .from(cluster)
                .leftJoin(subscribe)
                .on(subscribe.subscriber.id.eq(memberId))
                .where(
                        cluster.clusterNum.eq(
                                        queryFactory.select(cluster.clusterNum)
                                                .from(cluster)
                                                .where(cluster.member.id.eq(memberId))
                                )
                                .and(cluster.member.id.ne(memberId))
                                .and(subscribe.followed.id.isNull().or(subscribe.followed.id.ne(cluster.member.id)))
                )
                .fetch();

        return results;
    }

}
