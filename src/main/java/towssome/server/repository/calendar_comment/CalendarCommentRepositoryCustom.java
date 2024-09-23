package towssome.server.repository.calendar_comment;

import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarComment;

import java.time.LocalDate;
import java.util.List;

public interface CalendarCommentRepositoryCustom {

    List<CalendarComment> findCommentsByDay(LocalDate localDate, Calendar calendar);

    List<Integer> findCommentsInCalendarOfMonth(int year, int month, Calendar calendar);

}
