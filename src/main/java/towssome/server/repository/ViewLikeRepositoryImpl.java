package towssome.server.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import towssome.server.entity.ReviewPost;

import java.util.List;
import java.util.Objects;

import static towssome.server.entity.QReviewPost.reviewPost;
import static towssome.server.entity.QViewLike.viewLike;
@Repository
@RequiredArgsConstructor
public class ViewLikeRepositoryImpl implements ViewLikeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewPost> findLikeByMemberIdOrderByIdDesc(Long memberId, String sort, Pageable page) {
        List<ReviewPost> result = queryFactory
                .select(reviewPost)
                .from(reviewPost)
                .join(viewLike).on(reviewPost.id.eq(viewLike.reviewPost.id))
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.likeFlag.eq(true)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(viewLike.count())
                .from(viewLike)
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.likeFlag.eq(true)))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(result, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findLikeByIdAndMemberIdLessThanOrderByIdDesc(Long cursorId, Long memberId, String sort, Pageable page) {
        List<ReviewPost> result = queryFactory
                .select(reviewPost)
                .from(reviewPost)
                .join(viewLike).on(reviewPost.id.eq(viewLike.reviewPost.id))
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.likeFlag.eq(true))
                        .and(getLTGT(sort, cursorId)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(viewLike.count())
                .from(viewLike)
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.likeFlag.eq(true))
                        .and(getLTGT(sort, cursorId)))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(result, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findRecentByMemberIdOrderByIdDesc(Long memberId, String sort, Pageable page) {
        List<ReviewPost> result = queryFactory
                .select(reviewPost)
                .from(reviewPost)
                .join(viewLike).on(reviewPost.id.eq(viewLike.reviewPost.id))
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.viewFlag.eq(true)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory.select(viewLike.count())
                .from(viewLike)
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.viewFlag.eq(true)))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(result, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findRecentByMemberIdLessThanOrderByIdDesc(Long cursorId, Long memberId, String sort, Pageable page) {
        List<ReviewPost> result = queryFactory
                .select(reviewPost)
                .from(reviewPost)
                .join(viewLike).on(reviewPost.id.eq(viewLike.reviewPost.id))
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.viewFlag.eq(true))
                        .and(getLTGT(sort, cursorId)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(viewLike.count())
                .from(viewLike)
                .where(viewLike.member.id.eq(memberId)
                        .and(viewLike.viewFlag.eq(true))
                        .and(getLTGT(sort, cursorId)))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(result, page, count::fetchOne);
    }

    private OrderSpecifier<Long> getOrderSpecifier(String sort) {
        return Objects.equals(sort, "asc") ? viewLike.id.asc() : viewLike.id.desc();
    }

    private BooleanExpression getLTGT(String sort, Long cursorId){
        return Objects.equals(sort, "asc") ? viewLike.id.gt(cursorId) : viewLike.id.lt(cursorId);
    }
}
