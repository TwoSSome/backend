package towssome.server.repository.comment;

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
    public Page<Comment> findFirstCommentPage(Long reviewId, String sort, Pageable pageable) {
        List<Comment> results;
        results = queryFactory
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

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }

    @Override
    public Page<Comment> findCommentPageByCursorId(Long reviewId,Long cursorId, String sort, Pageable pageable) {
        List<Comment> results;
        results = queryFactory
                .selectFrom(comment)
                .where(reviewIdContains(reviewId), nextCommentId(cursorId, sort))
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(reviewIdContains(reviewId), nextCommentId(cursorId, sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }

    @Override
    public Comment findFixedCommentByReviewId(Long reviewId) {
        return queryFactory.selectFrom(comment)
                .where(comment.reviewPost.id.eq(reviewId)
                        .and(comment.fixFlag.isTrue()))
                .fetchOne();
    }



    private BooleanExpression reviewIdContains(Long reviewId) {
        return comment.reviewPost.id.eq(reviewId);
    }

    private com.querydsl.core.types.OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("desc".equalsIgnoreCase(sort)) {
            return comment.id.desc();
        } else {
            return comment.id.asc();
        }
    }

    private BooleanExpression nextCommentId(Long cursorId, String sort){
        if(cursorId == null) return null;
        if("desc".equalsIgnoreCase(sort)){
            return comment.id.lt(cursorId);
        }
        else {
            return comment.id.gt(cursorId);
        }
    }
}
