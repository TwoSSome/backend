package towssome.server.repository.calendar_post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.dto.CursorResult;
import towssome.server.dto.PhotoInPost;
import towssome.server.dto.PoomPoomLogInfo;
import towssome.server.entity.Member;
import towssome.server.enumrated.PostType;

import java.util.List;

import static towssome.server.entity.QCalendar.calendar;
import static towssome.server.entity.QCalendarPost.calendarPost;
import static towssome.server.entity.QCalendarSchedule.calendarSchedule;
import static towssome.server.entity.QPhoto.photo;

@RequiredArgsConstructor
public class CalendarPostRepositoryImpl implements CalendarPostRepositoryCustom {
    private final JPAQueryFactory QueryFactory;

    @Override
    public CursorResult<PoomPoomLogInfo> findPoomPoomLogs(Member jwtMember, int page, int size) {
        StringExpression profilePhotoPath = Expressions.stringTemplate("{0}", photo.s3Path);
        BooleanExpression condition = calendar.authMember1.eq(jwtMember).not()
                .and(calendarPost.author.eq(calendar.authMember2))
                .or(calendar.authMember2.eq(jwtMember)
                        .and(calendarPost.author.eq(calendar.authMember1)));

        List<PoomPoomLogInfo> content = QueryFactory
                .select(Projections.constructor(PoomPoomLogInfo.class,
                        calendarPost.id,
                        Projections.constructor(PhotoInPost.class, photo.id, profilePhotoPath),
                        calendarSchedule.startDate,
                        calendarSchedule.endDate))
                .from(calendarPost)
                .leftJoin(calendar).on(calendarPost.author.eq(calendar.authMember1))
                .leftJoin(photo).on(calendarPost.eq(photo.calendarPost))
                .leftJoin(calendarSchedule).on(calendarPost.calendarSchedule.eq(calendarSchedule))
                .where(calendarPost.postType.eq(PostType.MEMOIR)
                        .and(
                                calendarPost.author.eq(jwtMember)
                                        .or(condition)
                        )
                )
                .offset((long) page * size)
                .limit(size + 1)
                .orderBy(calendarSchedule.startDate.desc(), calendarPost.createDate.desc())
                .fetch();

        boolean hasNext = content.size() > size;
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new CursorResult<>(content, (long)page + 2, hasNext);
    }

    @Override
    public List<PoomPoomLogInfo> findPoomPoomLogsByMonth(Member jwtMember, int month) {
        StringExpression profilePhotoPath = Expressions.stringTemplate("{0}", photo.s3Path);
        NumberExpression<Integer> targetMonth = Expressions.numberTemplate(Integer.class, "{0}", month);
        BooleanExpression condition = calendar.authMember1.eq(jwtMember).not()
                .and(calendarPost.author.eq(calendar.authMember2))
                .or(calendar.authMember2.eq(jwtMember)
                        .and(calendarPost.author.eq(calendar.authMember1)));

        return QueryFactory
                .select(Projections.constructor(PoomPoomLogInfo.class,
                        calendarPost.id,
                        Projections.constructor(PhotoInPost.class, photo.id, profilePhotoPath),
                        calendarSchedule.startDate,
                        calendarSchedule.endDate))
                .from(calendarPost)
                .leftJoin(calendar).on(calendarPost.author.eq(calendar.authMember1))
                .leftJoin(photo).on(calendarPost.eq(photo.calendarPost))
                .leftJoin(calendarSchedule).on(calendarPost.calendarSchedule.eq(calendarSchedule))
                .where(calendarPost.postType.eq(PostType.MEMOIR)
                        .and(targetMonth.between(calendarSchedule.startDate.month(), calendarSchedule.endDate.month()))
                        .and(
                                calendarPost.author.eq(jwtMember)
                                        .or(condition)
                        )
                )
                .orderBy(calendarSchedule.startDate.asc(), calendarPost.createDate.desc())
                .fetch();
    }

}
