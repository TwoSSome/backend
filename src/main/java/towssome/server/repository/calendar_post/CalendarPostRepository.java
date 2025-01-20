package towssome.server.repository.calendar_post;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.CalendarPost;
import towssome.server.entity.CalendarSchedule;

import java.util.List;

public interface CalendarPostRepository extends JpaRepository<CalendarPost,Long>, CalendarPostRepositoryCustom {

    List<CalendarPost> findAllByCalendarSchedule(CalendarSchedule calendarSchedule);
}
