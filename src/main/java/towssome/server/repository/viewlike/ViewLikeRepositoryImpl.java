package towssome.server.repository.viewlike;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import towssome.server.dto.CursorResult;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.entity.ViewLike;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static towssome.server.entity.QReviewPost.reviewPost;
import static towssome.server.entity.QViewLike.viewLike;
@Repository
@RequiredArgsConstructor
public class ViewLikeRepositoryImpl implements ViewLikeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public CursorResult<ReviewPost> findLikeByMemberIdOrderByIdDesc(Long memberId, Long pageId, String sort, int size) {
        if(pageId == null) {
            List<ReviewPost> result = buildQuery(memberId, sort, size, null, false).fetch();

            return new CursorResult<>(
                    result,
                    2L,
                    hasNext(result, size));
        }
        else{
            Long offset = pageId * size;
            List<ReviewPost> result = buildQuery(memberId, sort, size, offset, false).fetch();

            return new CursorResult<>(
                    result,
                    pageId + 2,
                    hasNext(result, size));
        }
    }

    @Override
    public CursorResult<ReviewPost> findRecentByMemberIdOrderByIdDesc(Long memberId, Long pageId, String sort, int size) {
        if(pageId == null) {
            List<ReviewPost> result = buildQuery(memberId, sort, size, null, true).fetch();

            return new CursorResult<>(
                    result,
                    2L,
                    hasNext(result, size));
        }
        else{
            Long offset = pageId * size;
            List<ReviewPost> result = buildQuery(memberId, sort, size, offset, true).fetch();

            return new CursorResult<>(
                    result,
                    pageId + 2,
                    hasNext(result, size));
        }
    }

    @Override
    public Long findLikeAmountByReviewPost(Long reviewId) {
        return queryFactory
                .select(viewLike.count())
                .from(viewLike)
                .where(viewLike.reviewPost.id.eq(reviewId)
                        .and(viewLike.likeFlag.eq(Boolean.TRUE)))
                .fetchOne();
    }

    @Override
    public Long findViewAmountByReviewPost(Long reviewId) {
        return queryFactory
                .select(viewLike.count())
                .from(viewLike)
                .where(viewLike.reviewPost.id.eq(reviewId)
                        .and(viewLike.viewFlag.eq(Boolean.TRUE)))
                .fetchOne();
    }



    private JPAQuery<ReviewPost> buildQuery(Long memberId, String sort, int size, Long offset, boolean viewFlag) {
        JPAQuery<ReviewPost> query = queryFactory
                .select(reviewPost)
                .from(reviewPost)
                .join(viewLike).on(reviewPost.id.eq(viewLike.reviewPost.id))
                .where(viewLike.member.id.eq(memberId))
                .orderBy(getOrderSpecifier(sort))
                .limit(size + 1);

        if (offset != null) {
            query.offset(offset);
        }

        if(viewFlag) query.where(viewLike.viewFlag.eq(true));
        else query.where(viewLike.likeFlag.eq(true));


        return query;
    }

    private OrderSpecifier<LocalDateTime> getOrderSpecifier(String sort) {
        return Objects.equals(sort, "asc") ? viewLike.lastModifiedDate.asc() : viewLike.lastModifiedDate.desc();
    }

    private Boolean hasNext(List<ReviewPost> result, int size) {
        return result.size() > size;
    }


    @Override
    public List<Tuple> findViewLikesWithScores(Member member, List<Member> clusterMembers) {
        return queryFactory.select(
                        viewLike.reviewPost,
                        viewLike.member,
                        viewLike.likeFlag,
                        viewLike.viewFlag,
                        viewLike.reviewPost.id.count().multiply(0.2).add(1.0).as("likeWeight"),
                        viewLike.reviewPost.id.count().multiply(0.05).add(1.0).as("viewWeight")
                )
                .from(viewLike)
                .where(viewLike.member.in(clusterMembers))
                .fetch();
    }
}
