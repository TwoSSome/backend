package towssome.server.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import towssome.server.entity.Comment;

import java.util.List;

import static towssome.server.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Comment> findAllByReviewIdOrderBySort(String sort, Long reviewId, Pageable pageable) {
        List<Comment> getComments = queryFactory
                .selectFrom(comment)
                .where(reviewIdContains(reviewId))
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(reviewIdContains(reviewId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(getComments, pageable, count::fetchOne);
    }

    private BooleanExpression reviewIdContains(Long reviewId) {
        return comment.reviewPost.id.eq(reviewId);
    }

    private com.querydsl.core.types.OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("desc".equalsIgnoreCase(sort)) {
            return comment.createDate.desc();
        } else {
            return comment.createDate.asc();
        }
    }
}
