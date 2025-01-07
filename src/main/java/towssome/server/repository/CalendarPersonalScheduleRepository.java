package towssome.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import towssome.server.entity.Calendar;
import towssome.server.entity.CalendarPersonalSchedule;

import java.util.List;

public interface CalendarPersonalScheduleRepository extends JpaRepository<CalendarPersonalSchedule,Long> {

    List<CalendarPersonalSchedule> findAllByCalendar(Calendar calendar);

}
