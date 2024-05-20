package towssome.server.repository;

import com.querydsl.core.types.OrderSpecifier;
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
    public Page<ReviewPost> findAllByOrderByReviewIdDesc(Pageable page, String sort) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .orderBy(orderSpecifier(sort))
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
    public Page<ReviewPost> findByCursorIdLessThanOrderByReviewIdDesc(Long cursorId, String sort,Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.id.lt(cursorId))
                .orderBy(orderSpecifier(sort))
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
    public Page<ReviewPost> findMyPostAllByMemberId(Long memberId, String sort, Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.member.id.eq(memberId))
                .orderBy(reviewPost.id.desc())
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
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.member.id.eq(memberId).and(reviewPost.id.lt(cursorId)))
                .orderBy(orderSpecifier(sort))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(reviewPost.count())
                .from(reviewPost)
                .where(reviewPost.member.id.eq(memberId).and(reviewPost.id.lt(cursorId)))
                .offset(page.getOffset())
                .limit(page.getPageSize());

        return PageableExecutionUtils.getPage(reviewPosts, page, count::fetchOne);
    }

    private OrderSpecifier<Long> orderSpecifier(String sort){
        if(Objects.equals(sort, "asc")){
            return reviewPost.id.asc();
        }
        else{
            return reviewPost.id.desc();
        }
    }
}