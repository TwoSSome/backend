package towssome.server.repository.calendar_comment;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarComment;
import towssome.server.entity.QCalendarComment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import static towssome.server.entity.QCalendar.calendar;
import static towssome.server.entity.QCalendarComment.*;

@RequiredArgsConstructor
public class CalendarCommentRepositoryImpl implements CalendarCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CalendarComment> findCommentsByDay(LocalDate date, Calendar calendar) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return queryFactory
                .selectFrom(calendarComment)
                .where(calendarComment.createDate.between(startOfDay, endOfDay)
                        .and(calendarComment.calendar.eq(calendar)))
                .fetch();
    }

    @Override
    public List<Integer> findCommentsInCalendarOfMonth(int year, int month, Calendar calendar) {
        return queryFactory
                .select(Expressions.numberTemplate(Integer.class, "DAY({0})", calendarComment.createDate))
                .from(calendarComment)
                .where(
                        Expressions.numberTemplate(Integer.class, "YEAR({0})", calendarComment.createDate).eq(year),
                        Expressions.numberTemplate(Integer.class, "MONTH({0})", calendarComment.createDate).eq(month),
                        calendarComment.calendar.eq(calendar)
                )
                .groupBy(Expressions.numberTemplate(Integer.class, "DAY({0})", calendarComment.createDate))
                .fetch();
    }

}
