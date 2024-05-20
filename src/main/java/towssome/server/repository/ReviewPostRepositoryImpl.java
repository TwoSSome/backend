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
@Repository
@RequiredArgsConstructor
public class ReviewPostRepositoryImpl implements ReviewPostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewPost> findFirstPageByOrderByReviewIdDesc(Boolean recommend, Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .orderBy(reviewPost.id.desc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(reviewPost.count())
                .from(reviewPost)
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findByCursorIdLessThanOrderByReviewIdDesc(Long cursorId, Boolean recommend, Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .orderBy(reviewPost.id.desc())
                .where(reviewPost.id.lt(cursorId))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(reviewPost.count())
                .from(reviewPost)
                .where(reviewPost.id.lt(cursorId))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findMyPostFirstPageByMemberId(Long memberId, String sort, Pageable page) {
        List<ReviewPost> reviewPosts;
        reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.member.id.eq(memberId))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(reviewPost.count())
                .from(reviewPost)
                .where(reviewPost.member.id.eq(memberId))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    @Override
    public Page<ReviewPost> findByMemberIdLessThanOrderByIdDesc(Long memberId, Long cursorId, String sort, Pageable page) {
        List<ReviewPost> reviewPosts;
        JPAQuery<Long> count;
        reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.member.id.eq(memberId).and(getLTGT(sort, cursorId)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        count = queryFactory
                .select(reviewPost.count())
                .where(reviewPost.member.id.eq(memberId).and(getLTGT(sort, cursorId)))
                .orderBy(getOrderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    private OrderSpecifier<Long> getOrderSpecifier(String sort) {
        return Objects.equals(sort, "asc") ? reviewPost.id.asc() : reviewPost.id.desc();
    }

    private BooleanExpression getLTGT(String sort, Long cursorId){
        return Objects.equals(sort, "asc") ? reviewPost.id.gt(cursorId) : reviewPost.id.lt(cursorId);
    }
}