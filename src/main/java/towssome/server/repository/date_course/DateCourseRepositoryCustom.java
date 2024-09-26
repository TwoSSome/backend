package towssome.server.repository.date_course;

import towssome.server.dto.CalendarExistInMonth;
import towssome.server.dto.CalendarInMonthReq;
import towssome.server.entity.Calendar;
import towssome.server.entity.DateCourse;
import towssome.server.service.DateCourseByDateReq;

import java.util.List;

public interface DateCourseRepositoryCustom {

    List<DateCourse> findDateCourseByDate(DateCourseByDateReq req, Calendar calendar);

    List<Integer> ExistDateCourseByDate(CalendarInMonthReq req, Calendar calendar);

}
