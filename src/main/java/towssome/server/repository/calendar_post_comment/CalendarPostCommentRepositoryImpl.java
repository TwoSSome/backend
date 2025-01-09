package towssome.server.repository.calendar_post_comment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import towssome.server.entity.CalendarPostComment;

import java.util.List;
import static towssome.server.entity.QCalendarPostComment.calendarPostComment;

@Repository
@RequiredArgsConstructor
public class CalendarPostCommentRepositoryImpl implements CalendarPostCommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CalendarPostComment> findCPCPageByCursorId(Long postId, Long cursorId, String sort, Pageable pageable) {
        List<CalendarPostComment> results;
        results = queryFactory
                .selectFrom(calendarPostComment)
                .where(postIdContains(postId), nextCommentId(cursorId, sort))
                .orderBy(getOrderSpecifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(calendarPostComment.count())
                .from(calendarPostComment)
                .where(postIdContains(postId), nextCommentId(cursorId, sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return PageableExecutionUtils.getPage(results, pageable, count::fetchOne);
    }

    private BooleanExpression postIdContains(Long postId) {
        return calendarPostComment.calendarPost.id.eq(postId);
    }

    private com.querydsl.core.types.OrderSpecifier<?> getOrderSpecifier(String sort) {
        if ("asc".equalsIgnoreCase(sort)) return calendarPostComment.id.asc();
        else return calendarPostComment.id.desc();
    }

    private BooleanExpression nextCommentId(Long cursorId, String sort){
        if(cursorId == null) return null;
        if("asc".equalsIgnoreCase(sort)) return calendarPostComment.id.gt(cursorId);
        else return calendarPostComment.id.lt(cursorId);
    }
}
