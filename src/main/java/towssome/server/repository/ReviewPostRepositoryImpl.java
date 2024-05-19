package towssome.server.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import towssome.server.entity.ReviewPost;

import java.util.List;

import static towssome.server.entity.QReviewPost.reviewPost;
@Repository
@RequiredArgsConstructor
public class ReviewPostRepositoryImpl implements ReviewPostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewPost> findAllByOrderByReviewIdDesc(Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .orderBy(reviewPost.latsModifiedDate.desc())
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
    public Page<ReviewPost> findByCursorIdLessThanOrderByReviewIdDesc(Long cursorId, Pageable page) {
        List<ReviewPost> reviewPosts = queryFactory
                .selectFrom(reviewPost)
                .where(reviewPost.id.lt(cursorId))
                .orderBy(reviewPost.id.desc())
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
}
