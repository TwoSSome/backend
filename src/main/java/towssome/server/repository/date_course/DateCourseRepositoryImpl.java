package towssome.server.repository.date_course;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import towssome.server.dto.CalendarExistInMonth;
import towssome.server.dto.CalendarInMonthReq;
import towssome.server.entity.Calendar;
import towssome.server.entity.DateCourse;
import towssome.server.entity.QDateCourse;
import towssome.server.service.DateCourseByDateReq;

import java.util.List;

import static towssome.server.entity.QDateCourse.*;

@RequiredArgsConstructor
public class DateCourseRepositoryImpl implements DateCourseRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DateCourse> findDateCourseByDate(DateCourseByDateReq req, Calendar calendar) {
        return queryFactory
                .selectFrom(dateCourse)
                .where(dateCourse.date.registrationYear.between(req.startYear(), req.endYear()),
                        dateCourse.date.registrationMonth.between(req.startMonth(), req.endMonth()),
                        dateCourse.date.registrationDay.between(req.startDay(),req.endDay()),
                        dateCourse.calendar.eq(calendar))
                .fetch();
    }

    // 해당 연월 사이 + 캘린더를 만족하는 데이트코스의 날짜들을 중복 제거하여 반환
    @Override
    public List<Integer> ExistDateCourseByDate(CalendarInMonthReq req, Calendar calendar) {
        return queryFactory
                .select(dateCourse.date.registrationDay)
                .distinct()
                .from(dateCourse)
                .where(dateCourse.date.registrationYear.eq(req.year()),
                        dateCourse.date.registrationMonth.eq(req.month()),
                        dateCourse.calendar.eq(calendar))
                .fetch();
    }
}
